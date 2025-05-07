package com.example.messmanagement.models

data class LoginResponse(
    val token: String,
    val userId: Long,
    val name: String,
    val email: String,
    val role: String
)