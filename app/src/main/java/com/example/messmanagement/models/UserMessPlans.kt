package com.example.messmanagement.models

data class UserMessPlans(
    val id: Long? = null,
    //val user: User? = null,
    val messPlan: MessPlan? = null,
    val remainingDays: Int = 0,
    //val startDate: LocalDateTime? = null,
    //val expiryDate: LocalDateTime? = null,
    val status: PlanStatus? = null,
    //val skipDaysUsed: Int = 0  // Add this with a default value
) {
    enum class PlanStatus {
        ACTIVE,
        EXPIRED,
        SUSPENDED
    }
}