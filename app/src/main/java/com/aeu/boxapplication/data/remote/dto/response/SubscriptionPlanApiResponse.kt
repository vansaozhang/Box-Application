package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class SubscriptionPlanApiResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("frequency_in_days")
    val frequencyInDays: Int,

    @SerializedName("price")
    val price: Double,

    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("products")
    val products: List<ProductApiResponse>? = null
)

data class ProductApiResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("category")
    val category: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("sku")
    val sku: String?,

    @SerializedName("weight_kg")
    val weightKg: Double?,

    @SerializedName("dimensions")
    val dimensions: String?
)
