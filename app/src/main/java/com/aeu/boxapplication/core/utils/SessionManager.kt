package com.aeu.boxapplication.core.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

data class PendingPlanSelection(
    val id: String,
    val name: String,
    val price: String,
    val period: String,
    val features: List<String>
)

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

    fun savePendingPlan(
        planId: String,
        planName: String,
        planPrice: String,
        planPeriod: String,
        planFeatures: List<String> = emptyList()
    ) {
        prefs.edit()
            .putString("pending_plan_id", planId)
            .putString("pending_plan_name", planName)
            .putString("pending_plan_price", planPrice)
            .putString("pending_plan_period", planPeriod)
            .putString("pending_plan_features", JSONArray(planFeatures).toString())
            .apply()
    }

    fun getPendingPlanId(): String? = prefs.getString("pending_plan_id", null)
    fun getPendingPlanName(): String? = prefs.getString("pending_plan_name", null)
    fun getPendingPlanPrice(): String? = prefs.getString("pending_plan_price", null)
    fun getPendingPlanPeriod(): String? = prefs.getString("pending_plan_period", null)
    fun getPendingPlanFeatures(): List<String> {
        val raw = prefs.getString("pending_plan_features", null).orEmpty()
        if (raw.isBlank()) {
            return emptyList()
        }

        return runCatching {
            val jsonArray = JSONArray(raw)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    add(jsonArray.optString(index))
                }
            }
        }.getOrDefault(emptyList())
    }

    fun getPendingPlanSelection(): PendingPlanSelection? {
        val id = getPendingPlanId() ?: return null
        return PendingPlanSelection(
            id = id,
            name = getPendingPlanName() ?: "Pro",
            price = getPendingPlanPrice() ?: "$19",
            period = getPendingPlanPeriod() ?: "/mo",
            features = getPendingPlanFeatures()
        )
    }

    fun clearPendingPlan() {
        prefs.edit()
            .remove("pending_plan_id")
            .remove("pending_plan_name")
            .remove("pending_plan_price")
            .remove("pending_plan_period")
            .remove("pending_plan_features")
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
            .remove("pending_plan_features")
            .apply()
    }

    fun setHasAccount() {
        prefs.edit().putBoolean("has_account", true).apply()
    }

    fun hasAccount(): Boolean = prefs.getBoolean("has_account", false)
}
