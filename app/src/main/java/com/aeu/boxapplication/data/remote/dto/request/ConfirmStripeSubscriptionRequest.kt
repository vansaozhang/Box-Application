package com.aeu.boxapplication.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class ConfirmStripeSubscriptionRequest(
    @SerializedName("plan_id")
    val planId: String,
    @SerializedName("stripe_subscription_id")
    val stripeSubscriptionId: String
)
