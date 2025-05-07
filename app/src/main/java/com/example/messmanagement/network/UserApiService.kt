package com.example.messmanagement.network

import com.example.messmanagement.models.MessPlan
import com.example.messmanagement.models.UserProfile
import retrofit2.Call
import retrofit2.http.GET

interface UserApiService {
    @GET("user/profile")
    fun getUserProfile(): Call<UserProfile>

    @GET("user/mess-plans")
    fun getAvailableMessPlans(): Call<List<MessPlan>>
}