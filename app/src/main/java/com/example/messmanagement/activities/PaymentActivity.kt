package com.example.messmanagement.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messmanagement.adapters.PaymentAdapter
import com.example.messmanagement.databinding.ActivityPaymentBinding
import com.example.messmanagement.databinding.DialogAddPaymentBinding
import com.example.messmanagement.models.Payment
import com.example.messmanagement.network.ApiClient
import com.example.messmanagement.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var paymentAdapter: PaymentAdapter

    private val paymentService by lazy {
        ApiClient.Services.paymentService(tokenManager)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed with operations
            fetchPayments()
        } else {
            showError("Storage permission is required to download receipts")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        setupRecyclerView()
        setupAddPaymentButton()
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // For Android 9 (API 28) and below
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    fetchPayments()
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        } else {
            // For Android 10+ we don't need the permission for app-specific storage
            fetchPayments()
        }
    }

    private fun setupRecyclerView() {
        binding.paymentsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAddPaymentButton() {
        binding.addPaymentButton.setOnClickListener {
            showAddPaymentDialog()
        }
    }

    private fun showAddPaymentDialog() {
        val dialogBinding = DialogAddPaymentBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Add Payment")
            .setView(dialogBinding.root)
            .setPositiveButton("Submit") { _, _ ->
                val amount = dialogBinding.amountEditText.text.toString().trim()
                val utrNumber = dialogBinding.utrEditText.text.toString().trim()

                // Validate and submit payment
                if (validatePaymentInputs(amount, utrNumber)) {
                    submitPayment(amount, utrNumber)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun validatePaymentInputs(amount: String, utrNumber: String): Boolean {
        return when {
            amount.isEmpty() -> {
                showError("Amount cannot be empty")
                false
            }
            utrNumber.isEmpty() -> {
                showError("UTR Number cannot be empty")
                false
            }
            amount.toDoubleOrNull() == null -> {
                showError("Invalid amount")
                false
            }
            else -> true
        }
    }

    private fun submitPayment(amount: String, utrNumber: String) {
        // Disable submit button
        binding.addPaymentButton.isEnabled = false

        val payment = Payment(
            amount = BigDecimal(amount),
            utrNumber = utrNumber
        )

        paymentService.createPayment(payment)
            .enqueue(object : Callback<Payment> {
                override fun onResponse(
                    call: Call<Payment>,
                    response: Response<Payment>
                ) {
                    // Re-enable button
                    binding.addPaymentButton.isEnabled = true

                    if (response.isSuccessful) {
                        showSuccess("Payment added successfully")
                        fetchPayments()
                    } else {
                        showError("Failed to add payment")
                    }
                }

                override fun onFailure(call: Call<Payment>, t: Throwable) {
                    // Re-enable button
                    binding.addPaymentButton.isEnabled = true

                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun fetchPayments() {
        // Show loading state
        binding.paymentsRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        paymentService.getUserPayments()
            .enqueue(object : Callback<List<Payment>> {
                override fun onResponse(
                    call: Call<List<Payment>>,
                    response: Response<List<Payment>>
                ) {
                    // Hide progress bar
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        response.body()?.let { payments ->
                            // Detailed logging
                            Log.d("PaymentActivity", "Payments received: ${payments.size}")
                            payments.forEach { payment ->
                                Log.d("PaymentActivity", "Payment: $payment")
                            }

                            // Setup adapter
                            setupPaymentAdapter(payments)
                        } ?: run {
                            showEmptyState()
                        }
                    } else {
                        // Log error body
                        val errorBody = response.errorBody()?.string()
                        Log.e("PaymentActivity", "Error: $errorBody")
                        showError("Failed to load payments")
                        showEmptyState()
                    }
                }

                override fun onFailure(call: Call<List<Payment>>, t: Throwable) {
                    // Hide progress bar
                    binding.progressBar.visibility = View.GONE

                    Log.e("PaymentActivity", "Network error", t)
                    showError("Network error: ${t.message}")
                    showEmptyState()
                }
            })
    }

    private fun setupPaymentAdapter(payments: List<Payment>) {
        if (payments.isEmpty()) {
            showEmptyState()
            return
        }

        // Show RecyclerView
        binding.paymentsRecyclerView.visibility = View.VISIBLE
        binding.emptyStateTextView.visibility = View.GONE

        paymentAdapter = PaymentAdapter(payments)
        binding.paymentsRecyclerView.adapter = paymentAdapter
    }

    private fun showEmptyState() {
        binding.paymentsRecyclerView.visibility = View.GONE
        binding.emptyStateTextView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}