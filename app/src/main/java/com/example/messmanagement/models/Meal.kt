package com.example.messmanagement.models

import java.time.LocalDate

data class Meal(
    val id: Long? = null,
    val mealType: MealType? = null,
    val menuItems: String? = null,
    val mealDate: List<Int>? = null,
    val menuItemsList: List<String>? = null
) {
    enum class MealType {
        BREAKFAST,
        LUNCH,
        DINNER
    }
}