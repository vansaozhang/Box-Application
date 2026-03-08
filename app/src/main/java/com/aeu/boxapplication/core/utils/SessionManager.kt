package com.aeu.boxapplication.core.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class PendingPlanSelection(
    val id: String,
    val name: String,
    val price: String,
    val period: String,
    val features: List<String>
)

data class LocalShippingAddress(
    val id: String,
    val label: String,
    val address: String,
    val phone: String? = null,
    val createdAt: String? = null
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

    fun saveUserDetail(
        name: String,
        email: String? = null,
        phone: String? = null,
        token: String? = null
    ) {
        val editor = prefs.edit()
        editor.putString("user_name", name)
        editor.putString("user_email", email)
        editor.putString("user_phone", phone)
        token?.let { editor.putString("auth_token", it) }
        editor.apply()
    }

    fun saveUser(
        name: String,
        email: String? = null,
        phone: String? = null,
        token: String? = null
    ) {
        val editor = prefs.edit()
        editor.putString("user_name", name)
        editor.putString("user_email", email)
        editor.putString("user_phone", phone)
        token?.let { editor.putString("auth_token", it) }
        editor.apply()
    }

    fun getUserName(): String? = prefs.getString("user_name", null)

    fun getUserEmail(): String? = prefs.getString("user_email", null)

    fun getUserPhone(): String? = prefs.getString("user_phone", null)

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

    fun clearSession() {
        prefs.edit()
            .remove("user_name")
            .remove("user_email")
            .remove("user_phone")
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

    fun getLocalShippingAddresses(): List<LocalShippingAddress> {
        val storageKey = localShippingAddressesKey() ?: return emptyList()
        val raw = prefs.getString(storageKey, null).orEmpty()
        if (raw.isBlank()) {
            return emptyList()
        }

        return runCatching {
            val jsonArray = JSONArray(raw)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val item = jsonArray.optJSONObject(index) ?: continue
                    add(
                        LocalShippingAddress(
                            id = item.optString("id").ifBlank { UUID.randomUUID().toString() },
                            label = item.optString("label").ifBlank { "Address" },
                            address = item.optString("address"),
                            phone = item.optString("phone").takeIf { it.isNotBlank() && it != "null" },
                            createdAt = item.optString("created_at").takeIf { it.isNotBlank() && it != "null" }
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun addLocalShippingAddress(
        label: String,
        address: String,
        phone: String? = null,
        makeDefault: Boolean = false
    ): LocalShippingAddress {
        val newAddress = LocalShippingAddress(
            id = UUID.randomUUID().toString(),
            label = label.trim(),
            address = address.trim(),
            phone = phone?.trim()?.takeIf { it.isNotBlank() },
            createdAt = System.currentTimeMillis().toString()
        )

        val updatedAddresses = buildList {
            add(newAddress)
            addAll(getLocalShippingAddresses())
        }

        persistLocalShippingAddresses(updatedAddresses)
        if (makeDefault || getPreferredShippingAddressId().isNullOrBlank()) {
            setPreferredShippingAddressId(newAddress.id)
        }
        return newAddress
    }

    fun getPreferredShippingAddressId(): String? {
        val key = preferredShippingAddressKey() ?: return null
        return prefs.getString(key, null)
    }

    fun setPreferredShippingAddressId(addressId: String?) {
        val key = preferredShippingAddressKey() ?: return
        val editor = prefs.edit()
        if (addressId.isNullOrBlank()) {
            editor.remove(key)
        } else {
            editor.putString(key, addressId)
        }
        editor.apply()
    }

    private fun persistLocalShippingAddresses(addresses: List<LocalShippingAddress>) {
        val storageKey = localShippingAddressesKey() ?: return
        val payload = JSONArray().apply {
            addresses.forEach { address ->
                put(
                    JSONObject().apply {
                        put("id", address.id)
                        put("label", address.label)
                        put("address", address.address)
                        put("phone", address.phone)
                        put("created_at", address.createdAt)
                    }
                )
            }
        }.toString()

        prefs.edit()
            .putString(storageKey, payload)
            .apply()
    }

    private fun localShippingAddressesKey(): String? {
        val userKey = getUserEmail()
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotBlank() }
            ?: getUserPhone()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
            ?: getAuthToken()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
            ?: return null

        return "local_shipping_addresses_$userKey"
    }

    private fun preferredShippingAddressKey(): String? {
        val userKey = getUserEmail()
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotBlank() }
            ?: getUserPhone()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
            ?: getAuthToken()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
            ?: return null

        return "preferred_shipping_address_$userKey"
    }
}
