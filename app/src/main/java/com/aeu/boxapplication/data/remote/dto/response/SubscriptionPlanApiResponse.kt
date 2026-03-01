package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class SubscriptionPlanApiResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("frequency_in_days")
    val frequencyInDays: Int,

    @SerializedName("price")
    val price: Double
)
