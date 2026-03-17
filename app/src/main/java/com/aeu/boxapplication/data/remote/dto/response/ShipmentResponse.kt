package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ShipmentResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("subscription_id")
    val subscriptionId: String,

    @SerializedName("shipment_date")
    val shipmentDate: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("tracking_number")
    val trackingNumber: String?
)
