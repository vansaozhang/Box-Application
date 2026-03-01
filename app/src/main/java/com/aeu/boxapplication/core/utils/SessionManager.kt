package com.aeu.boxapplication.core.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("boxly_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // --- FIX: Save both Name and Email ---
    fun saveUserDetail(name: String, email: String, token: String? = null) {
        val editor = prefs.edit()
        editor.putString("user_name", name)
        editor.putString("user_email", email)
        token?.let { editor.putString("auth_token", it) }
        editor.apply()
    }
    // If you want to use .saveUser(), this line must look like this:
    fun saveUser(name: String, email: String, token: String? = null) {
        val editor = prefs.edit()
        editor.putString("user_name", name)
        editor.putString("user_email", email)
        token?.let { editor.putString("auth_token", it) }
        editor.apply()
    }

    fun getUserName(): String? = prefs.getString("user_name", null)

    // --- FIX: This will now return the actual saved email ---
    fun getUserEmail(): String? = prefs.getString("user_email", null)

    fun getAuthToken(): String? = prefs.getString("auth_token", null)

    fun savePendingPlan(planId: String, planName: String, planPrice: String, planPeriod: String) {
        prefs.edit()
            .putString("pending_plan_id", planId)
            .putString("pending_plan_name", planName)
            .putString("pending_plan_price", planPrice)
            .putString("pending_plan_period", planPeriod)
            .apply()
    }

    fun getPendingPlanId(): String? = prefs.getString("pending_plan_id", null)
    fun getPendingPlanName(): String? = prefs.getString("pending_plan_name", null)
    fun getPendingPlanPrice(): String? = prefs.getString("pending_plan_price", null)
    fun getPendingPlanPeriod(): String? = prefs.getString("pending_plan_period", null)

    fun clearPendingPlan() {
        prefs.edit()
            .remove("pending_plan_id")
            .remove("pending_plan_name")
            .remove("pending_plan_price")
            .remove("pending_plan_period")
            .apply()
    }

    // --- FIX: Logout should remove Name, Email, and Token ---
    fun clearSession() {
        prefs.edit()
            .remove("user_name")
            .remove("user_email")
            .remove("auth_token")
            .remove("pending_plan_id")
            .remove("pending_plan_name")
            .remove("pending_plan_price")
            .remove("pending_plan_period")
            .apply()
    }

    fun setHasAccount() {
        prefs.edit().putBoolean("has_account", true).apply()
    }

    fun hasAccount(): Boolean = prefs.getBoolean("has_account", false)
}
