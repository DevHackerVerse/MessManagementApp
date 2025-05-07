package com.example.messmanagement.adapters

import android.R
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.messmanagement.databinding.ItemPaymentBinding
import com.example.messmanagement.models.Payment
import com.example.messmanagement.utils.PdfGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentAdapter(
    private val payments: List<Payment>
) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            binding.apply {
                // Amount
                amountTextView.text = "Amount: ₹${payment.amount ?: 0.00}"

                // UTR Number
                utrNumberTextView.text = "UTR: ${payment.utrNumber ?: "N/A"}"

                // Status
                statusTextView.text = "Status: ${payment.status ?: "Unknown"}"

                // Payment Date
                val paymentDate = payment.paymentDate?.let {
                    "${it[2]}/${it[1]}/${it[0]}"
                } ?: "N/A"
                dateTextView.text = "Date: $paymentDate"

                // Optional: Highlight pending payments
                itemView.setBackgroundColor(
                    if (payment.pending == true)
                        ContextCompat.getColor(itemView.context, android.R.color.holo_orange_light)
                    else
                        ContextCompat.getColor(itemView.context, android.R.color.transparent)
                )

                // Setup download button click listener
                downloadReceiptButton.setOnClickListener {
                    generateAndSharePdf(payment)
                }

                // Show download button only for successful payments
                downloadReceiptButton.visibility = if (payment.status == "SUCCESS") {
                    ViewGroup.VISIBLE
                } else {
                    ViewGroup.GONE
                }
            }
        }

        private fun generateAndSharePdf(payment: Payment) {
            val context = itemView.context

            // Show loading toast
            Toast.makeText(context, "Generating receipt...", Toast.LENGTH_SHORT).show()

            // Generate PDF in background
            CoroutineScope(Dispatchers.IO).launch {
                val file = PdfGenerator.generatePaymentReceipt(context, payment)

                withContext(Dispatchers.Main) {
                    if (file != null) {
                        // Share the PDF file
                        try {
                            val fileUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )

                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(fileUri, "application/pdf")
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }

                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Unable to open PDF. Make sure you have a PDF viewer installed.",
                                Toast.LENGTH_LONG
                            ).show()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to generate receipt",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(payments[position])
    }

    override fun getItemCount() = payments.size
}