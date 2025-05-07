package com.example.messmanagement.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messmanagement.databinding.ActivityRegisterBinding
import com.example.messmanagement.models.RegisterRequest
import com.example.messmanagement.models.LoginResponse
import com.example.messmanagement.network.ApiClient
import com.example.messmanagement.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    // View Binding
    private lateinit var binding: ActivityRegisterBinding

    // Token Manager
    private lateinit var tokenManager: TokenManager

    // API Service
    private val authService by lazy {
        ApiClient.Services.authService(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Token Manager
        tokenManager = TokenManager(this)

        // Setup Register Button Listener
        setupRegisterListener()

        // Setup Login Text Listener
        setupLoginListener()
    }

    private fun setupRegisterListener() {
        binding.btnRegister.setOnClickListener {
            performRegistration()
        }
    }

    private fun setupLoginListener() {
        binding.loginText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun performRegistration() {
        // Get input values
        val name = binding.ETFullname.text.toString().trim()
        val email = binding.ETEmail.text.toString().trim()
        val password = binding.ETPassword.text.toString().trim()
        val confirmPassword = binding.ETConfirmPassword.text.toString().trim()

        // Validate inputs
        if (validateInputs(name, email, password, confirmPassword)) {
            // Disable register button to prevent multiple clicks
            binding.btnRegister.isEnabled = false

            // Create register request
            val registerRequest = RegisterRequest(name, email, password)

            // Perform registration API call
            authService.register(registerRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    // Re-enable register button
                    binding.btnRegister.isEnabled = true

                    when {
                        response.isSuccessful -> {
                            response.body()?.let { loginResponse ->
                                // Save token
                                tokenManager.saveToken(loginResponse.token)

                                // Navigate to Dashboard
                                navigateToDashboard()
                            }
                        }
                        response.code() == 400 -> {
                            showError("Registration failed. User may already exist.")
                        }
                        else -> {
                            showError("Registration failed. Please try again.")
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Re-enable register button
                    binding.btnRegister.isEnabled = true

                    // Show network error
                    showError("Network error: ${t.message}")
                }
            })
        }
    }

    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                binding.tlFullname.error = "Name cannot be empty"
                false
            }
            email.isEmpty() -> {
                binding.tlEmail.error = "Email cannot be empty"
                false
            }
            password.isEmpty() -> {
                binding.tlPassword.error = "Password cannot be empty"
                false
            }
            confirmPassword.isEmpty() -> {
                binding.tlConfirmPassword.error = "Confirm password cannot be empty"
                false
            }
            password != confirmPassword -> {
                binding.tlConfirmPassword.error = "Passwords do not match"
                false
            }
            password.length < 6 -> {
                binding.tlPassword.error = "Password must be at least 6 characters"
                false
            }
            else -> true
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        // Clear previous tasks
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}