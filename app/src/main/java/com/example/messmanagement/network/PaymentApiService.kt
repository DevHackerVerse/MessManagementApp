package com.example.messmanagement.network

import com.example.messmanagement.models.Payment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PaymentApiService {
    @POST("payments")
    fun createPayment(@Body payment: Payment): Call<Payment>

    @GET("payments")
    fun getUserPayments(): Call<List<Payment>>
}