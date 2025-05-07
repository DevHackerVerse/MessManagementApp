package com.example.messmanagement.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messmanagement.databinding.ActivityLoginBinding
import com.example.messmanagement.models.LoginRequest
import com.example.messmanagement.models.LoginResponse
import com.example.messmanagement.network.ApiClient
import com.example.messmanagement.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    // View Binding
    private lateinit var binding: ActivityLoginBinding

    // Token Manager
    private lateinit var tokenManager: TokenManager

    // API Service
    private val authService by lazy {
        ApiClient.Services.authService(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Token Manager
        tokenManager = TokenManager(this)

        // Check if user is already logged in
        checkExistingLogin()

        // Setup Login Button Listener
        setupLoginListener()

        // Setup Register Text Listener
        setupRegisterListener()
    }

    private fun checkExistingLogin() {
        val existingToken = tokenManager.getToken()
        if (!existingToken.isNullOrBlank()) {
            navigateToDashboard()
        }
    }

    private fun setupLoginListener() {
        binding.loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun setupRegisterListener() {
        binding.signUpText.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun performLogin() {
        // Get input values
        val email = binding.ETEmail.text.toString().trim()
        val password = binding.ETPassword.text.toString().trim()

        // Validate inputs
        if (validateInputs(email, password)) {
            // Disable login button to prevent multiple clicks
            binding.loginButton.isEnabled = false

            // Create login request
            val loginRequest = LoginRequest(email, password)

            // Perform login API call
            authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    // Re-enable login button
                    binding.loginButton.isEnabled = true

                    when {
                        response.isSuccessful -> {
                            response.body()?.let { loginResponse ->
                                // Save token
                                tokenManager.saveToken(loginResponse.token)

                                // Navigate to Dashboard
                                navigateToDashboard()
                            }
                        }
                        response.code() == 401 -> {
                            showError("Invalid credentials")
                        }
                        else -> {
                            showError("Login failed. Please try again.")
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Re-enable login button
                    binding.loginButton.isEnabled = true

                    // Show network error
                    showError("Network error: ${t.message}")
                }
            })
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.tlEmail.error = "Email cannot be empty"
                false
            }
            password.isEmpty() -> {
                binding.tlPassword.error = "Password cannot be empty"
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

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}