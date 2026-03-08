package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class StripeCheckoutIntentResponse(
    @SerializedName("customer_id")
    val customerId: String,
    @SerializedName("ephemeral_key")
    val ephemeralKey: String,
    @SerializedName("payment_intent_client_secret")
    val paymentIntentClientSecret: String?,
    @SerializedName("setup_intent_client_secret")
    val setupIntentClientSecret: String?,
    @SerializedName("requires_payment_sheet")
    val requiresPaymentSheet: Boolean = true,
    @SerializedName("stripe_subscription_status")
    val stripeSubscriptionStatus: String? = null,
    @SerializedName("stripe_subscription_id")
    val stripeSubscriptionId: String,
    @SerializedName("publishable_key")
    val publishableKey: String,
    @SerializedName("stripe_price_id")
    val stripePriceId: String,
    @SerializedName("plan_id")
    val planId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("currency")
    val currency: String
)
