package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String?,

    @SerializedName("user")
    val user: UserData?
)

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)