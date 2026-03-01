package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AuthMeResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String
)
