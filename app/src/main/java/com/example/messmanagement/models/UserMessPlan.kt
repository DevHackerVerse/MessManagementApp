package com.example.messmanagement.models

import java.math.BigDecimal

data class UserMessPlan(
    val id: Long? = null,
    val messPlan: MessPlan? = null,
    val remainingDays: Int = 0,
    val status: String? = null,
    val skipDaysUsed: Int? = 0,  // Ensure this matches backend
    val totalPlanAmount: BigDecimal? = null,
    val remainingPlanAmount: BigDecimal? = null
)