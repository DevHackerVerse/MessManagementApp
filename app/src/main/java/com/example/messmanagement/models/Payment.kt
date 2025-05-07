package com.example.messmanagement.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class Payment(
    val id: Long? = null,
    val amount: BigDecimal? = null,
    val utrNumber: String? = null,
    val status: String? = null,
    val paymentDate: List<Int>? = null,
    val verificationDate: List<Int>? = null,
    val remarks: String? = null,
    val pending: Boolean? = null,
    val validPayment: Boolean? = null
) {
    enum class PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED
    }


}