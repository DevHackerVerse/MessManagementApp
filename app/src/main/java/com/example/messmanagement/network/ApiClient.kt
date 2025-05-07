package com.example.messmanagement.network

import com.example.messmanagement.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {


    companion object {
        // Use your PC's local IP address
        private const val BASE_URL = "http://192.168.139.158:8080/api/"

        // Logging Interceptor
        private fun createLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        // Authentication Interceptor
        private fun createAuthInterceptor(tokenManager: TokenManager): Interceptor {
            return Interceptor { chain ->
                val originalRequest = chain.request()
                val token = tokenManager.getToken()

                val newRequest = originalRequest.newBuilder().apply {
                    token?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                    addHeader("Content-Type", "application/json")
                }.build()

                chain.proceed(newRequest)
            }
        }

        // OkHttpClient
        private fun createOkHttpClient(tokenManager: TokenManager): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(createLoggingInterceptor())
                .addInterceptor(createAuthInterceptor(tokenManager))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        // Retrofit Instance
        fun createRetrofit(tokenManager: TokenManager): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(createOkHttpClient(tokenManager))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // API Service Creators
        inline fun <reified T> createService(tokenManager: TokenManager): T {
            return createRetrofit(tokenManager).create(T::class.java)
        }
    }

    // Specific Service Creators
    object Services {
        fun authService(tokenManager: TokenManager): AuthApiService {
            return createService(tokenManager)
        }

        fun userService(tokenManager: TokenManager): UserApiService {
            return createService(tokenManager)
        }

        fun messPlanService(tokenManager: TokenManager): MessPlanApiService {
            return createService(tokenManager)
        }

        fun mealBookingService(tokenManager: TokenManager): MealBookingApiService {
            return createService(tokenManager)
        }

        fun paymentService(tokenManager: TokenManager): PaymentApiService {
            return createService(tokenManager)
        }

        fun feedbackService(tokenManager: TokenManager): FeedbackApiService {
            return createService(tokenManager)
        }
    }
}