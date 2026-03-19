package com.aeu.boxapplication.domain.model

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val description: String,
    val tier: SubscriptionTier,
    val price: Double,
    val frequency: String, // weekly, biweekly, monthly, quarterly
    val features: List<String>,
    val productCount: Int,
    val isActive: Boolean,
    val imageUrl: String?,
    val products: List<Product> = emptyList()
)
