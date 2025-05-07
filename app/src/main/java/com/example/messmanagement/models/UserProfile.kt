package com.example.messmanagement.models

data class UserProfile(
    val id: Long? = null,
    val name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val userMessPlan: UserMessPlan? = null
)