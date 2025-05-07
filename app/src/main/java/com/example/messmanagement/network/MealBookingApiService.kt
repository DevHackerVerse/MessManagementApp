package com.example.messmanagement.network

import com.example.messmanagement.models.Booking
import com.example.messmanagement.models.BookingRequest
import com.example.messmanagement.models.Meal
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MealBookingApiService {
    @GET("meals/date")
    fun getMealsByDate(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int
    ): Call<List<Meal>>

    @POST("bookings")
    fun createBooking(@Body bookingRequest: BookingRequest): Call<Booking>
}