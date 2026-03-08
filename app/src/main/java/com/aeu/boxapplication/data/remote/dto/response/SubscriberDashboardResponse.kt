package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class SubscriberDashboardResponse(
    @SerializedName("billing")
    val billing: DashboardBillingResponse,

    @SerializedName("latest_shipment")
    val latestShipment: DashboardShipmentResponse?,

    @SerializedName("active_subscriptions")
    val activeSubscriptions: List<DashboardSubscriptionResponse>
)

data class DashboardBillingResponse(
    @SerializedName("next_billing_date")
    val nextBillingDate: String?,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("subscription_count")
    val subscriptionCount: Int
)

data class DashboardShipmentResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("subscription_id")
    val subscriptionId: String,

    @SerializedName("subscription_name")
    val subscriptionName: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("shipment_date")
    val shipmentDate: String,

    @SerializedName("estimated_delivery_date")
    val estimatedDeliveryDate: String?,

    @SerializedName("tracking_number")
    val trackingNumber: String?,

    @SerializedName("progress")
    val progress: Double,

    @SerializedName("steps")
    val steps: List<DashboardShipmentStepResponse>
)

data class DashboardShipmentStepResponse(
    @SerializedName("key")
    val key: String,

    @SerializedName("label")
    val label: String,

    @SerializedName("completed")
    val completed: Boolean,

    @SerializedName("current")
    val current: Boolean
)

data class DashboardSubscriptionResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("plan_id")
    val planId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("recharge_date")
    val rechargeDate: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("frequency_in_days")
    val frequencyInDays: Int,

    @SerializedName("billing_status")
    val billingStatus: String
)
