package com.aeu.boxapplication.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") // MUST be "email"
    val email: String,

    @SerializedName("password")
    val password: String
)