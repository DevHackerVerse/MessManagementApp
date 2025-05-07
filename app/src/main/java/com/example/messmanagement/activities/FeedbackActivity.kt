package com.example.messmanagement.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messmanagement.adapters.FeedbackAdapter
import com.example.messmanagement.databinding.ActivityFeedbackBinding
import com.example.messmanagement.databinding.DialogSubmitFeedbackBinding
import com.example.messmanagement.models.Feedback
import com.example.messmanagement.network.ApiClient
import com.example.messmanagement.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var feedbackAdapter: FeedbackAdapter

    private val feedbackService by lazy {
        ApiClient.Services.feedbackService(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        setupRecyclerView()
        setupSubmitFeedbackButton()
        fetchFeedbacks()
    }

    private fun setupRecyclerView() {
        binding.feedbackRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSubmitFeedbackButton() {
        binding.submitFeedbackButton.setOnClickListener {
            showSubmitFeedbackDialog()
        }
    }

    private fun showSubmitFeedbackDialog() {
        val dialogBinding = DialogSubmitFeedbackBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Submit Feedback")
            .setView(dialogBinding.root)
            .setPositiveButton("Submit") { _, _ ->
                val message = dialogBinding.feedbackEditText.text.toString().trim()

                // Validate and submit feedback
                if (validateFeedbackInput(message)) {
                    submitFeedback(message)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun validateFeedbackInput(message: String): Boolean {
        return if (message.isEmpty()) {
            showError("Feedback cannot be empty")
            false
        } else {
            true
        }
    }

    private fun submitFeedback(message: String) {
        // Disable submit button
        binding.submitFeedbackButton.isEnabled = false

        // Create feedback using the specific constructor
        val feedback = Feedback(message)

        feedbackService.submitFeedback(feedback)
            .enqueue(object : Callback<Feedback> {
                override fun onResponse(
                    call: Call<Feedback>,
                    response: Response<Feedback>
                ) {
                    // Re-enable submit button
                    binding.submitFeedbackButton.isEnabled = true

                    if (response.isSuccessful) {
                        showSuccess("Feedback submitted successfully")
                        fetchFeedbacks()
                    } else {
                        // Log error body for debugging
                        val errorBody = response.errorBody()?.string()
                        Log.e("FeedbackActivity", "Submit Error: $errorBody")
                        showError("Failed to submit feedback")
                    }
                }

                override fun onFailure(call: Call<Feedback>, t: Throwable) {
                    // Re-enable submit button
                    binding.submitFeedbackButton.isEnabled = true

                    // Log the full exception
                    Log.e("FeedbackActivity", "Network error", t)
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun fetchFeedbacks() {
        // Hide RecyclerView during loading
        binding.feedbackRecyclerView.visibility = View.GONE

        feedbackService.getUserFeedbacks()
            .enqueue(object : Callback<List<Feedback>> {
                override fun onResponse(
                    call: Call<List<Feedback>>,
                    response: Response<List<Feedback>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { feedbacks ->
                            // Detailed logging
                            Log.d("FeedbackActivity", "Feedbacks received: ${feedbacks.size}")
                            feedbacks.forEach { feedback ->
                                Log.d("FeedbackActivity", "Feedback: $feedback")
                            }

                            // Setup adapter
                            setupFeedbackAdapter(feedbacks)
                        } ?: run {
                            showEmptyState()
                        }
                    } else {
                        // Log error body
                        val errorBody = response.errorBody()?.string()
                        Log.e("FeedbackActivity", "Error: $errorBody")
                        showError("Failed to load feedbacks")
                        showEmptyState()
                    }
                }

                override fun onFailure(call: Call<List<Feedback>>, t: Throwable) {
                    Log.e("FeedbackActivity", "Network error", t)
                    showError("Network error: ${t.message}")
                    showEmptyState()
                }
            })
    }

    private fun setupFeedbackAdapter(feedbacks: List<Feedback>) {
        if (feedbacks.isEmpty()) {
            showEmptyState()
            return
        }

        // Show RecyclerView
        binding.feedbackRecyclerView.visibility = View.VISIBLE
        binding.emptyStateTextView.visibility = View.GONE

        feedbackAdapter = FeedbackAdapter(feedbacks)
        binding.feedbackRecyclerView.adapter = feedbackAdapter
    }

    private fun showEmptyState() {
        binding.feedbackRecyclerView.visibility = View.GONE
        binding.emptyStateTextView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}