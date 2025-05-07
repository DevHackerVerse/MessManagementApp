package com.example.messmanagement.network

import com.example.messmanagement.models.UserMessPlan
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MessPlanApiService {
    @POST("user/mess-plans/{planId}/book")
    fun bookMessPlan(@Path("planId") planId: Long): Call<UserMessPlan>

    @POST("user-mess-plans/skip-day")
    fun skipDay(@Query("userMessPlanId") userMessPlanId: Long): Call<UserMessPlan>

        @GET("user/mess-plan")
        fun getUserMessPlan(): Call<UserMessPlan>


}