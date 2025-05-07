package com.example.messmanagement.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messmanagement.databinding.ActivityDashboardBinding
import com.example.messmanagement.models.UserProfile
import com.example.messmanagement.network.ApiClient
import com.example.messmanagement.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var tokenManager: TokenManager
    private val userService by lazy {
        ApiClient.Services.userService(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Token Manager
        tokenManager = TokenManager(this)

        // Fetch User Profile
        fetchUserProfile()

        // Setup Dashboard Actions
        setupDashboardActions()
    }

    private fun fetchUserProfile() {
        userService.getUserProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        updateUserProfile(profile)
                    }
                } else {
                    showError("Failed to load profile")
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun updateUserProfile(profile: UserProfile) {
        // Set user name
        binding.userNameTextView.text = profile.name ?: "User"

        // Update Mess Plan Section
        profile.userMessPlan?.let { userMessPlan ->
            binding.messPlanCardView.visibility = View.VISIBLE

            // Safely access mess plan name
            val planName = userMessPlan.messPlan?.name ?: "No Plan Selected"
            binding.messPlanNameTextView.text = planName

            // Set Remaining Days
            binding.remainingDaysTextView.text = "Remaining Days: ${userMessPlan.remainingDays}"

            // Detailed logging
            Log.d("DashboardActivity", "Mess Plan: $planName")
            Log.d("DashboardActivity", "Remaining Days: ${userMessPlan.remainingDays}")
        } ?: run {
            // Hide Mess Plan Card if no plan is selected
            binding.messPlanCardView.visibility = View.GONE
        }
    }

    private fun setupDashboardActions() {
        // Logout
        binding.logoutButton.setOnClickListener {
            performLogout()
        }

        // Book Meal
        binding.bookMealCard.setOnClickListener {
            navigateToMealBooking()
        }

        // View Payments
        binding.paymentsCard.setOnClickListener {
            navigateToPayments()
        }

        // Skip Day
        binding.skipDayCard.setOnClickListener {
            navigateToSkipDay()
        }

        // Feedback
        binding.feedbackCard.setOnClickListener {
            navigateToFeedback()
        }
        // Mess Plan Button
        binding.messPlanCard.setOnClickListener {
            navigateToMessPlans()
        }
        binding.detailsTV.setOnClickListener{
            navigateToSkipDay()

        }
    }

    private fun performLogout() {
        // Clear token
        tokenManager.clearToken()

        // Navigate to Login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    private fun navigateToMessPlans() {
        startActivity(Intent(this, MessPlanActivity::class.java))
    }

    private fun navigateToMealBooking() {
        startActivity(Intent(this, MealBookingActivity::class.java))
    }

    private fun navigateToPayments() {
        startActivity(Intent(this, PaymentActivity::class.java))
    }

    private fun navigateToSkipDay() {
        startActivity(Intent(this, SkipDayActivity::class.java))
    }

    private fun navigateToFeedback() {
        startActivity(Intent(this, FeedbackActivity::class.java))
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}