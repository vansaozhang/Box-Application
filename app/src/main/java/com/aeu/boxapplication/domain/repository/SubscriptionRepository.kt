package com.aeu.boxapplication.domain.repository

import com.aeu.boxapplication.core.network.NetworkResult
import com.aeu.boxapplication.domain.model.Subscription
import com.aeu.boxapplication.domain.model.SubscriptionPlan

interface SubscriptionRepository {
    suspend fun getSubscriptionPlans(): NetworkResult<List<SubscriptionPlan>>
    suspend fun getSubscriptionPlan(planId: String): NetworkResult<SubscriptionPlan>
    suspend fun getMySubscription(): NetworkResult<Subscription>
    suspend fun createSubscription(planId: String): NetworkResult<Subscription>
}
