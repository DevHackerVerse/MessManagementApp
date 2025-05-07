package com.example.messmanagement.models

import java.time.LocalDateTime


data class Booking(
    val id: Long? = null,
    val bookingDate: List<Int>? = null,
    val remarks: String? = null,
    val status: String? = null
) {
    enum class BookingStatus {
        CONFIRMED,
        CANCELLED,
        SKIPPED
    }
}
