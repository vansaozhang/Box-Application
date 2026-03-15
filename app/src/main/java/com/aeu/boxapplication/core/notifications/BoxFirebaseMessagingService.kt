package com.aeu.boxapplication.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aeu.boxapplication.MainActivity
import com.aeu.boxapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BoxFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        const val CHANNEL_ID_ORDER = "order_notifications"
        const val CHANNEL_ID_GENERAL = "general_notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        
        // Store token locally
        saveFcmToken(token)
        
        // Send token to backend (will be called when user logs in)
        NotificationTokenManager.setToken(this, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        Log.d(TAG, "Message received from: ${message.from}")
        
        val handledNotificationPayload = message.notification?.let { notification ->
            val title = notification.title ?: "Box Subscription"
            val body = notification.body ?: ""
            
            showNotification(title, body, message.data)
            true
        } ?: false
        
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${message.data}")
            if (!handledNotificationPayload) {
                handleDataMessage(message.data)
            }
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return
        
        when (type) {
            "order_success" -> {
                val title = data["title"] ?: "Order Successful!"
                val body = data["body"] ?: "Your order has been confirmed"
                showNotification(title, body, data)
            }
            "shipment" -> {
                val title = data["title"] ?: "Order Shipped"
                val body = data["body"] ?: "Your order has been shipped"
                showNotification(title, body, data)
            }
            "renewal_reminder" -> {
                val title = data["title"] ?: "Renewal Reminder"
                val body = data["body"] ?: "Your subscription will renew soon"
                showNotification(title, body, data)
            }
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        createNotificationChannels(notificationManager)
        
        val channelId = when (data["type"]) {
            "order_success", "shipment" -> CHANNEL_ID_ORDER
            else -> CHANNEL_ID_GENERAL
        }
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", data["type"])
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val orderChannel = NotificationChannel(
                CHANNEL_ID_ORDER,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for orders and shipments"
                enableVibration(true)
            }
            
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
            }
            
            notificationManager.createNotificationChannel(orderChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    private fun saveFcmToken(token: String) {
        val prefs = getSharedPreferences("box_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }
}
