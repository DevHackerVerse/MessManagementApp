package com.example.messmanagement.network

import com.example.messmanagement.models.LoginRequest
import com.example.messmanagement.models.LoginResponse
import com.example.messmanagement.models.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<LoginResponse>

    @GET("auth/validate")
    fun validateToken(@Header("Authorization") token: String): Call<Boolean>
}