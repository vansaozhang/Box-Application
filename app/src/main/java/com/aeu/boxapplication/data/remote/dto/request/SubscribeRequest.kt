package com.aeu.boxapplication.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class SubscribeRequest(
    @SerializedName("plan_id")
    val planId: String
)
