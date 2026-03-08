package com.aeu.boxapplication.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateMyAddressRequest(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("address")
    val address: String
)
