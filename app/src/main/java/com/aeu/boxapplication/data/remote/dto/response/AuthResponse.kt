package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String?,

    @SerializedName("user")
    val user: UserData?
)

data class UserData(
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
    val status: String?
)
