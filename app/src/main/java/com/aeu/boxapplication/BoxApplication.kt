package com.aeu.boxapplication

import android.app.Application
import androidx.work.Configuration
import com.aeu.boxapplication.core.notifications.FcmTokenRegistrar
import com.aeu.boxapplication.core.notifications.NotificationTokenManager
import com.aeu.boxapplication.core.utils.SessionManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
// import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp  // Temporarily disabled
class BoxApplication : Application(), Configuration.Provider {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        initializeFirebaseMessaging()
    }

    private fun initializeFirebaseMessaging() {
        // Request FCM token on app startup
        applicationScope.launch {
            try {
                // Get FCM token
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        android.util.Log.d("BoxApplication", "FCM Token: $token")

                        NotificationTokenManager.setToken(this@BoxApplication, token)

                        // Register with backend if user is logged in
                        val sessionManager = SessionManager.getInstance(this@BoxApplication)
                        if (!sessionManager.getAuthToken().isNullOrEmpty()) {
                            FcmTokenRegistrar.registerTokenWithBackend(this@BoxApplication)
                        }
                    } else {
                        android.util.Log.e("BoxApplication", "Failed to get FCM token", task.exception)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("BoxApplication", "Error initializing FCM", e)
            }
        }
    }
}
