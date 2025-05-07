package com.example.messmanagement.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messmanagement.R
import com.example.messmanagement.adapters.MealAdapter
import com.example.messmanagement.databinding.ActivityMealBookingBinding
import com.example.messmanagement.models.Booking
import com.example.messmanagement.models.BookingRequest
import com.example.messmanagement.models.Meal
import com.example.messmanagement.network.ApiClient
import com.example.messmanagement.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class MealBookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealBookingBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var mealAdapter: MealAdapter

    // Use LocalDate for more robust date handling
    private var selectedDate: LocalDate = LocalDate.now()

    private val mealBookingService by lazy {
        ApiClient.Services.mealBookingService(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMealBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup Date Selection
        setupDateSelection()

        // Fetch Meals for Today
        fetchMealsByDate()

        // Setup Booking Button
        setupBookingButton()
    }

    private fun setupRecyclerView() {
        binding.mealsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupDateSelection() {
        // Update initial date display
        updateSelectedDateDisplay()

        // Date selection button
        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.time = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Update selected date
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

                // Update date display and fetch meals
                updateSelectedDateDisplay()
                fetchMealsByDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Optional: Set date range
        // datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun updateSelectedDateDisplay() {
        val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        binding.selectedDateTextView.text = "Selected Date: $formattedDate"
    }

    private fun fetchMealsByDate() {
        // Convert LocalDate to individual components
        val year = selectedDate.year
        val month = selectedDate.monthValue
        val day = selectedDate.dayOfMonth

        mealBookingService.getMealsByDate(year, month, day)
            .enqueue(object : Callback<List<Meal>> {
                override fun onResponse(
                    call: Call<List<Meal>>,
                    response: Response<List<Meal>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { meals ->
                            // Detailed logging
                            Log.d("MealBookingActivity", "Meals received: ${meals.size}")
                            meals.forEach { meal ->
                                Log.d("MealBookingActivity", "Meal: $meal")
                            }

                            // Setup adapter
                            setupMealAdapter(meals)
                        } ?: run {
                            showEmptyState()
                        }
                    } else {
                        // Log error body
                        val errorBody = response.errorBody()?.string()
                        Log.e("MealBookingActivity", "Error: $errorBody")

                        // Handle specific error codes
                        when (response.code()) {
                            403 -> showError("Authentication failed. Please log in again.")
                            404 -> showError("No meals found for selected date")
                            else -> showError("Failed to load meals: ${errorBody ?: "Unknown error"}")
                        }

                        showEmptyState()
                    }
                }

                override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                    Log.e("MealBookingActivity", "Network error", t)
                    showError("Network error: ${t.message}")
                    showEmptyState()
                }
            })
    }

    private fun setupMealAdapter(meals: List<Meal>) {
        if (meals.isEmpty()) {
            showEmptyState()
            return
        }

        // Show RecyclerView
        binding.mealsRecyclerView.visibility = View.VISIBLE
        binding.noMealsTextView.visibility = View.GONE

        mealAdapter = MealAdapter(meals) { selectedMeal ->
            // Optional: Handle individual meal selection
            Log.d("MealBookingActivity", "Selected Meal: $selectedMeal")
        }
        binding.mealsRecyclerView.adapter = mealAdapter
    }

    private fun setupBookingButton() {
        binding.bookMealButton.setOnClickListener {
            bookSelectedMeals()
        }
    }

    private fun bookSelectedMeals() {
        val selectedMeals = mealAdapter.getSelectedMeals()

        if (selectedMeals.isEmpty()) {
            showError("Please select at least one meal")
            return
        }

        // Book each selected meal
        selectedMeals.forEach { meal ->
            meal.id?.let { mealId ->
                val bookingRequest = BookingRequest(mealId)

                mealBookingService.createBooking(bookingRequest)
                    .enqueue(object : Callback<Booking> {
                        override fun onResponse(
                            call: Call<Booking>,
                            response: Response<Booking>
                        ) {
                            if (response.isSuccessful) {
                                showSuccess("Meal booked successfully")
                            } else {
                                showError("Failed to book meal")
                            }
                        }

                        override fun onFailure(call: Call<Booking>, t: Throwable) {
                            showError("Network error: ${t.message}")
                        }
                    })
            }
        }
    }

    private fun showEmptyState() {
        binding.mealsRecyclerView.visibility = View.GONE
        binding.noMealsTextView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}