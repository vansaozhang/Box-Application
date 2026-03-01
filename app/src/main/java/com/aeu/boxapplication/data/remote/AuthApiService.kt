package com.aeu.boxapplication.data.remote

import com.aeu.boxapplication.data.remote.dto.response.AuthResponse
import com.aeu.boxapplication.data.remote.dto.response.AuthMeResponse
import com.aeu.boxapplication.data.remote.dto.request.LoginRequest
import com.aeu.boxapplication.data.remote.dto.request.RegisterRequest
import com.aeu.boxapplication.data.remote.dto.request.SubscribeRequest
import com.aeu.boxapplication.data.remote.dto.response.SubscriptionApiResponse
import com.aeu.boxapplication.data.remote.dto.response.SubscriptionPlanApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") authHeader: String): Response<AuthMeResponse>

    @GET("subscription-plans")
    suspend fun getSubscriptionPlans(
        @Header("Authorization") authHeader: String,
        @Query("billingCycle") billingCycle: String? = null
    ): Response<List<SubscriptionPlanApiResponse>>

    @POST("subscription-plans/seed-defaults")
    suspend fun seedDefaultPlans(
        @Header("Authorization") authHeader: String
    ): Response<List<SubscriptionPlanApiResponse>>

    @GET("subscriptions/me")
    suspend fun getMySubscription(
        @Header("Authorization") authHeader: String
    ): Response<SubscriptionApiResponse?>

    @POST("subscriptions/subscribe")
    suspend fun subscribeToPlan(
        @Header("Authorization") authHeader: String,
        @Body request: SubscribeRequest
    ): Response<SubscriptionApiResponse>
}
