package com.aeu.boxapplication.core.notifications

import android.content.Context
import android.util.Log
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FcmTokenRegistrar {
    private const val TAG = "FcmTokenRegistrar"

    /**
     * Register FCM token with backend
     * Should be called after successful login
     */
    fun registerTokenWithBackend(context: Context) {
        val authToken = SessionManager.getInstance(context).getAuthToken()
        if (authToken.isNullOrEmpty()) {
            Log.w(TAG, "User not logged in, cannot register FCM token")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = NotificationTokenManager.getToken(context)
                    ?: NotificationTokenManager.refreshToken(context)

                if (token == null) {
                    Log.w(TAG, "No FCM token available to register")
                    return@launch
                }

                if (NotificationTokenManager.isTokenSent(context)) {
                    Log.d(TAG, "FCM token already registered with backend")
                    return@launch
                }

                val response = RetrofitClient.authApiService.updateFcmToken(
                    authHeader = "Bearer $authToken",
                    fcmToken = mapOf("fcm_token" to token)
                )

                Log.d(TAG, "FCM Registration - URL: ${RetrofitClient::class.java.getDeclaredField("BASE_URL").also { it.isAccessible = true }.get(null)}")
                Log.d(TAG, "FCM Registration - Status: ${response.code()}")
                Log.d(TAG, "FCM Registration - Body: ${response.body()}")
                Log.d(TAG, "FCM Registration - Error: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body()?.get("success") == true) {
                    NotificationTokenManager.markTokenAsSent(context)
                    Log.d(TAG, "FCM token registered successfully with backend")
                } else {
                    Log.e(TAG, "Failed to register FCM token: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error registering FCM token with backend", e)
            }
        }
    }

    /**
     * Refresh and register FCM token
     * Should be called on app startup if user is logged in
     */
    suspend fun refreshAndRegisterToken(context: Context) {
        withContext(Dispatchers.IO) {
            val token = NotificationTokenManager.refreshToken(context)
            
            if (token != null) {
                registerTokenWithBackend(context)
            }
        }
    }
}
