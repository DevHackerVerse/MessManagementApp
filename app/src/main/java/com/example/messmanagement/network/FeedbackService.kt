package com.example.messmanagement.network

import com.example.messmanagement.models.Feedback
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FeedbackApiService {
    @POST("feedbacks")
    fun submitFeedback(@Body feedback: Feedback): Call<Feedback>

    @GET("feedbacks")
    fun getUserFeedbacks(): Call<List<Feedback>>
}