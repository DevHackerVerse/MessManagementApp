package com.example.messmanagement.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("JWT_TOKEN", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("JWT_TOKEN", null)
    }

    fun clearToken() {
        prefs.edit().remove("JWT_TOKEN").apply()
    }

    companion object {
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            if (instance == null) {
                instance = TokenManager(context)
            }
            return instance!!
        }

        fun getToken(context: Context): String? {
            return getInstance(context).getToken()
        }
    }
}