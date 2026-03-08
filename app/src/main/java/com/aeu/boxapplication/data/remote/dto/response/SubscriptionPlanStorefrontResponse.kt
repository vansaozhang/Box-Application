package com.aeu.boxapplication.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class SubscriptionPlanStorefrontResponse(
    @SerializedName("categories")
    val categories: List<String>,

    @SerializedName("featured_plan")
    val featuredPlan: SubscriptionPlanStorefrontItemResponse?,

    @SerializedName("plans")
    val plans: List<SubscriptionPlanStorefrontItemResponse>
)

data class SubscriptionPlanStorefrontItemResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("plan_name")
    val planName: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("subtitle")
    val subtitle: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("period_label")
    val periodLabel: String,

    @SerializedName("features")
    val features: List<String>?,

    @SerializedName("badge")
    val badge: String?,

    @SerializedName("featured_label")
    val featuredLabel: String?,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("is_featured")
    val isFeatured: Boolean
)
