package com.example.messmanagement.models

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val role: UserRole
)

enum class UserRole {
    USER,
    ADMIN
}