package com.example.messmanagement.models

import java.time.LocalDateTime

data class Feedback(
    val id: Long? = null,
    val message: String? = null,
    val createdAt: List<Int>? = null,
    val status: String? = null,
    val adminResponse: String? = null
) {
    // Constructor for creating a new feedback
    constructor(message: String) : this(
        id = null,
        message = message,
        createdAt = null,
        status = "PENDING",
        adminResponse = null
    )
}