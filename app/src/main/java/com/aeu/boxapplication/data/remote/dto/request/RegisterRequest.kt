package com.aeu.boxapplication.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("password")
    val password: String,
)
