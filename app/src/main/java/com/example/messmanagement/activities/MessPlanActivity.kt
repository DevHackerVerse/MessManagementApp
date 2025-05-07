package com.example.messmanagement.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messmanagement.R
import com.example.messmanagement.adapters.MessPlanAdapter
import com.example.messmanagement.databinding.ActivityMessPlanBinding
import com.example.messmanagement.models.MessPlan
import com.example.messmanagement.models.UserMessPlan
import com.example.messmanagement.network.ApiClient
import com.example.messmanagement.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessPlanBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var messPlanAdapter: MessPlanAdapter

    private val userService by lazy {
        ApiClient.Services.userService(tokenManager)
    }

    private val messPlanService by lazy {
        ApiClient.Services.messPlanService(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        setupRecyclerView()
        fetchAvailableMessPlans()
    }

    private fun setupRecyclerView() {
        binding.messPlansRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchAvailableMessPlans() {
        userService.getAvailableMessPlans().enqueue(object : Callback<List<MessPlan>> {
            override fun onResponse(call: Call<List<MessPlan>>, response: Response<List<MessPlan>>) {
                if (response.isSuccessful) {
                    response.body()?.let { messPlans ->
                        setupMessPlanAdapter(messPlans)
                    }
                } else {
                    showError("Failed to load mess plans")
                }
            }

            override fun onFailure(call: Call<List<MessPlan>>, t: Throwable) {
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun setupMessPlanAdapter(messPlans: List<MessPlan>) {
        messPlanAdapter = MessPlanAdapter(messPlans) { selectedPlan ->
            bookMessPlan(selectedPlan)
        }
        binding.messPlansRecyclerView.adapter = messPlanAdapter
    }

    private fun bookMessPlan(messPlan: MessPlan) {
        messPlan.id?.let { planId ->
            messPlanService.bookMessPlan(planId).enqueue(object : Callback<UserMessPlan> {
                override fun onResponse(
                    call: Call<UserMessPlan>,
                    response: Response<UserMessPlan>
                ) {
                    if (response.isSuccessful) {
                        showSuccess("Mess Plan booked successfully")
                        finish() // Return to previous screen
                    } else {
                        showError("Failed to book mess plan")
                    }
                }

                override fun onFailure(call: Call<UserMessPlan>, t: Throwable) {
                    showError("Network error: ${t.message}")
                }
            })
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}