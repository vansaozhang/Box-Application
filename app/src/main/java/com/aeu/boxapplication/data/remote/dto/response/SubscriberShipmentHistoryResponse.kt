package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class SubscriberShipmentHistoryItemResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("plan_name")
    val planName: String,

    @SerializedName("shipment_date")
    val shipmentDate: String?,

    @SerializedName("status")
    val status: String,

    @SerializedName("tracking_number")
    val trackingNumber: String?,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("currency")
    val currency: String
)

data class SubscriberShipmentAddressResponse(
    @SerializedName("contact_name")
    val contactName: String,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("address")
    val address: String?
)

data class SubscriberShipmentHistoryDetailResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("plan_name")
    val planName: String,

    @SerializedName("shipment_date")
    val shipmentDate: String?,

    @SerializedName("status")
    val status: String,

    @SerializedName("tracking_number")
    val trackingNumber: String?,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("payment_status")
    val paymentStatus: String,

    @SerializedName("payment_date")
    val paymentDate: String?,

    @SerializedName("subscription_status")
    val subscriptionStatus: String,

    @SerializedName("subscription_start_date")
    val subscriptionStartDate: String?,

    @SerializedName("subscription_end_date")
    val subscriptionEndDate: String?,

    @SerializedName("period_label")
    val periodLabel: String,

    @SerializedName("shipping_address")
    val shippingAddress: SubscriberShipmentAddressResponse
)
