package com.aeu.boxapplication.core.notifications

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object NotificationTokenManager {
    private const val TAG = "NotificationTokenManager"
    private const val PREFS_NAME = "box_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"
    private const val KEY_TOKEN_SENT = "fcm_token_sent"

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    fun setToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_FCM_TOKEN, token)
            .putBoolean(KEY_TOKEN_SENT, false)
            .apply()
    }

    fun markTokenAsSent(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_TOKEN_SENT, true).apply()
    }

    fun isTokenSent(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_TOKEN_SENT, false)
    }

    suspend fun refreshToken(context: Context): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            setToken(context, token)
            Log.d(TAG, "FCM token refreshed: $token")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token", e)
            null
        }
    }
}
