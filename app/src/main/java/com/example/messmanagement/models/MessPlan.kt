package com.example.messmanagement.models

import java.math.BigDecimal

data class MessPlan(
    val id: Long? = null,
    val name: String? = null,
    val totalDays: Int? = null,
    val price: BigDecimal? = null
)