package com.aeu.boxapplication.presentation.subscription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.getMySubscriptionSafely
import com.aeu.boxapplication.data.remote.dto.request.ConfirmStripeSubscriptionRequest
import com.aeu.boxapplication.data.remote.dto.request.CreateStripeCheckoutIntentRequest
import com.aeu.boxapplication.data.remote.dto.request.SubscribeRequest
import com.aeu.boxapplication.data.remote.dto.response.StripeCheckoutIntentResponse
import com.aeu.boxapplication.data.remote.dto.response.SubscriptionPlanApiResponse
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

enum class BillingCycle(val apiValue: String, val periodLabel: String) {
    MONTHLY("monthly", "/mo"),
    YEARLY("yearly", "/yr")
}

data class PlanFeatureUi(
    val text: String,
    val included: Boolean
)

data class PlanUiModel(
    val id: String,
    val name: String,
    val subtitle: String,
    val priceLabel: String,
    val periodLabel: String,
    val features: List<PlanFeatureUi>
)

class SubscriptionViewModel(
    private val authService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set
    var isSubmitting by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var selectedCycle by mutableStateOf(BillingCycle.MONTHLY)
        private set
    var plans by mutableStateOf<List<PlanUiModel>>(emptyList())
        private set

    private var seedAttempted = false

    fun loadPlans(cycle: BillingCycle = selectedCycle) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            errorMessage = "Please login first."
            return
        }

        selectedCycle = cycle
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = authService.getSubscriptionPlans(
                    authHeader = "Bearer $token",
                    billingCycle = cycle.apiValue
                )

                if (response.isSuccessful) {
                    val body = response.body().orEmpty()
                    if (body.isEmpty() && !seedAttempted) {
                        seedAttempted = true
                        seedDefaultsAndReload(token, cycle)
                    } else {
                        plans = body.map { it.toUi(cycle) }
                    }
                } else {
                    errorMessage = "Failed to load plans"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun seedDefaultsAndReload(token: String, cycle: BillingCycle) {
        val seedResponse = authService.seedDefaultPlans("Bearer $token")
        if (!seedResponse.isSuccessful) {
            errorMessage = "Failed to seed default plans"
            return
        }

        val reload = authService.getSubscriptionPlans(
            authHeader = "Bearer $token",
            billingCycle = cycle.apiValue
        )
        if (reload.isSuccessful) {
            plans = reload.body().orEmpty().map { it.toUi(cycle) }
        } else {
            errorMessage = "Failed to load plans after seeding"
        }
    }

    fun setBillingCycle(isMonthly: Boolean) {
        val target = if (isMonthly) BillingCycle.MONTHLY else BillingCycle.YEARLY
        if (target != selectedCycle) {
            loadPlans(target)
        }
    }

    fun restorePurchases(
        onHasSubscription: () -> Unit,
        onNoSubscription: () -> Unit
    ) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            errorMessage = "Please login first."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = authService.getMySubscriptionSafely("Bearer $token")
                if (response.isSuccessful || response.code == 404) {
                    if (response.hasActiveSubscription) {
                        onHasSubscription()
                    } else {
                        onNoSubscription()
                        errorMessage = "No active subscription found."
                    }
                } else {
                    errorMessage = response.parseError ?: "Failed to restore purchases"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun subscribeToPlan(planId: String, onSuccess: () -> Unit) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            errorMessage = "Please login first."
            return
        }

        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null
            try {
                val response = authService.subscribeToPlan(
                    authHeader = "Bearer $token",
                    request = SubscribeRequest(planId = planId)
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = parseApiError(
                        errorBody = response.errorBody()?.string(),
                        fallback = "Failed to subscribe"
                    )
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isSubmitting = false
            }
        }
    }

    fun createStripeCheckout(
        planId: String,
        onReady: (StripeCheckoutIntentResponse) -> Unit
    ) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            errorMessage = "Please login first."
            return
        }

        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null
            try {
                val response = authService.createStripeCheckoutIntent(
                    authHeader = "Bearer $token",
                    request = CreateStripeCheckoutIntentRequest(planId = planId)
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        onReady(body)
                    } else {
                        errorMessage = "Failed to initialize Stripe checkout"
                    }
                } else {
                    errorMessage = parseApiError(
                        errorBody = response.errorBody()?.string(),
                        fallback = "Failed to initialize Stripe checkout"
                    )
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isSubmitting = false
            }
        }
    }

    fun confirmStripeSubscription(
        planId: String,
        stripeSubscriptionId: String,
        onSuccess: () -> Unit
    ) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            errorMessage = "Please login first."
            return
        }

        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null
            try {
                val response = authService.confirmStripeSubscription(
                    authHeader = "Bearer $token",
                    request = ConfirmStripeSubscriptionRequest(
                        planId = planId,
                        stripeSubscriptionId = stripeSubscriptionId
                    )
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = parseApiError(
                        errorBody = response.errorBody()?.string(),
                        fallback = "Failed to confirm subscription"
                    )
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isSubmitting = false
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }

    fun setError(message: String) {
        errorMessage = message
    }

    private fun parseApiError(errorBody: String?, fallback: String): String {
        if (errorBody.isNullOrBlank()) {
            return fallback
        }

        val extracted = runCatching {
            val json = JSONObject(errorBody)
            val messageValue = json.opt("message")
            when (messageValue) {
                is JSONArray -> {
                    buildList {
                        for (index in 0 until messageValue.length()) {
                            add(messageValue.optString(index))
                        }
                    }.firstOrNull { it.isNotBlank() }
                }

                else -> json.optString("message")
            }
        }.getOrNull()?.takeIf { it.isNotBlank() && it != "null" } ?: errorBody

        val normalized = extracted.lowercase(Locale.US)
        return when {
            "already has an active subscription" in normalized ->
                "You already have an active subscription."

            else -> extracted
        }
    }
}

private fun SubscriptionPlanApiResponse.toUi(cycle: BillingCycle): PlanUiModel {
    val priceValue = if (price % 1.0 == 0.0) {
        price.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", price)
    }

    return PlanUiModel(
        id = id,
        name = name,
        subtitle = subtitleFor(name),
        priceLabel = "$$priceValue",
        periodLabel = cycle.periodLabel,
        features = featuresFor(name)
    )
}

private fun subtitleFor(name: String): String = when (name.lowercase(Locale.US)) {
    "starter" -> "FOR INDIVIDUALS"
    "pro" -> "FOR POWER USERS"
    "business" -> "FOR TEAMS"
    else -> "PLAN"
}

private fun featuresFor(name: String): List<PlanFeatureUi> = when (name.lowercase(Locale.US)) {
    "starter" -> listOf(
        PlanFeatureUi("5 recurring orders", true),
        PlanFeatureUi("Basic analytics", true),
        PlanFeatureUi("Free shipping", false)
    )

    "pro" -> listOf(
        PlanFeatureUi("Unlimited recurring orders", true),
        PlanFeatureUi("Advanced analytics", true),
        PlanFeatureUi("Free shipping on all orders", true),
        PlanFeatureUi("Priority support", true)
    )

    "business" -> listOf(
        PlanFeatureUi("Everything in Pro", true),
        PlanFeatureUi("Multiple user seats", true),
        PlanFeatureUi("Dedicated account manager", true)
    )

    else -> emptyList()
}
