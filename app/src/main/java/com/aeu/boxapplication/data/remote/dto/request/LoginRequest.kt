package com.aeu.boxapplication.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("password")
    val password: String
)
