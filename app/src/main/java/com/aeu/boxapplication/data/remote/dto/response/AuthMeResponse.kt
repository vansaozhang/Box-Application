package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AuthMeResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("phone_number")
    val phoneNumber: String?,

    @SerializedName("role")
    val role: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("profile_image_url")
    val profileImageUrl: String?,

    @SerializedName("created_at")
    val createdAt: String?
)
