package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class SubscriberAddressResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("address")
    val address: String,

    @SerializedName("created_at")
    val createdAt: String?
)
