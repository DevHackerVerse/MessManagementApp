package com.example.messmanagement.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.messmanagement.databinding.ActivitySkipDayBinding
import com.example.messmanagement.models.UserMessPlan
import com.example.messmanagement.models.UserProfile
import com.example.messmanagement.network.ApiClient
import com.example.messmanagement.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SkipDayActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySkipDayBinding
    private lateinit var tokenManager: TokenManager
    private var userMessPlanId: Long? = null

    private val userService by lazy {
        ApiClient.Services.userService(tokenManager)
    }

    private val messPlanService by lazy {
        ApiClient.Services.messPlanService(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySkipDayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Fetch User Profile to get Mess Plan details
        fetchUserProfile()

        // Setup Skip Day Button
        setupSkipDayButton()
    }

    private fun fetchUserProfile() {
        userService.getUserProfile()
            .enqueue(object : Callback<UserProfile> {
                override fun onResponse(
                    call: Call<UserProfile>,
                    response: Response<UserProfile>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { profile ->
                            // Extract and update UI with user mess plan
                            val userMessPlan = profile.userMessPlan
                            if (userMessPlan != null) {
                                // Store the userMessPlanId for skip day request
                                userMessPlanId = userMessPlan.id
                                updateMessPlanUI(userMessPlan)
                            } else {
                                showError("No active mess plan found")
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("SkipDayActivity", "Error: $errorBody")
                        showError("Failed to load user profile")
                    }
                }

                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    Log.e("SkipDayActivity", "Network error", t)
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun updateMessPlanUI(userMessPlan: UserMessPlan) {
        binding.apply {
            // Mess Plan Details
            messPlanNameTextView.text = userMessPlan.messPlan?.name ?: "No Plan Name"

            // Total Days and Remaining Days
            val totalDays = userMessPlan.messPlan?.totalDays ?: 0
            val remainingDays = userMessPlan.remainingDays
            totalDaysTextView.text = "Total Days: $totalDays"
            remainingDaysTextView.text = "Remaining Days: $remainingDays"

            // Skip Days
            val usedSkipDays = userMessPlan.skipDaysUsed ?: 0
            skipDaysUsedTextView.text = "Skip Days Used: $usedSkipDays / 5"

            // Plan Price
            val planPrice = userMessPlan.messPlan?.price
            priceTextView.text = "Plan Price: ₹${planPrice ?: "N/A"}"

            // Plan Status
            statusTextView.text = "Status: ${userMessPlan.status ?: "Unknown"}"

            // Skip Day Button
            skipDayButton.isEnabled = usedSkipDays < 5
        }
    }

    private fun setupSkipDayButton() {
        binding.skipDayButton.setOnClickListener {
            showSkipDayConfirmationDialog()
        }
    }

    private fun showSkipDayConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Skip Day")
            .setMessage("Are you sure you want to skip a day?")
            .setPositiveButton("Yes") { _, _ ->
                performSkipDay()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    private fun performSkipDay() {
        val messPlanId = userMessPlanId
        if (messPlanId == null) {
            showError("No active mess plan found")
            return
        }

        messPlanService.skipDay(messPlanId)
            .enqueue(object : Callback<UserMessPlan> {
                override fun onResponse(
                    call: Call<UserMessPlan>,
                    response: Response<UserMessPlan>
                ) {
                    if (response.isSuccessful) {
                        showSuccess("Day skipped successfully")
                        fetchUserProfile()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("SkipDayActivity", "Skip Day Error: $errorBody")
                        showError("Failed to skip day: ${errorBody ?: "Unknown error"}")
                    }
                }

                override fun onFailure(call: Call<UserMessPlan>, t: Throwable) {
                    Log.e("SkipDayActivity", "Network error", t)
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}