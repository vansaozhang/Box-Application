package com.aeu.boxapplication.core.network

import com.aeu.boxapplication.data.remote.dto.request.*
import com.aeu.boxapplication.data.remote.dto.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
    
    @POST("auth/refresh-token")
    suspend fun refreshToken(@Header("Authorization") token: String): Response<AuthResponse>
    
    // User Management
    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserResponse>
    
    @PUT("users/profile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<UserResponse>
    
    @GET("users/subscribers")
    suspend fun getSubscribers(@Query("page") page: Int): Response<List<UserResponse>>
    
    // Subscription Plans
    @GET("subscriptions/plans")
    suspend fun getSubscriptionPlans(): Response<List<SubscriptionResponse>>
    
    @GET("subscriptions/plans/{id}")
    suspend fun getSubscriptionPlan(@Path("id") planId: String): Response<SubscriptionResponse>
    
    @POST("subscriptions/plans")
    suspend fun createSubscriptionPlan(@Body request: SubscriptionRequest): Response<SubscriptionResponse>
    
    @PUT("subscriptions/plans/{id}")
    suspend fun updateSubscriptionPlan(
        @Path("id") planId: String,
        @Body request: SubscriptionRequest
    ): Response<SubscriptionResponse>
    
    // Subscription Management
    @GET("subscriptions/my-subscription")
    suspend fun getMySubscription(): Response<SubscriptionResponse>
    
    @POST("subscriptions/subscribe")
    suspend fun createSubscription(@Body request: SubscriptionRequest): Response<SubscriptionResponse>
    
    @PUT("subscriptions/{id}/pause")
    suspend fun pauseSubscription(@Path("id") subscriptionId: String): Response<SubscriptionResponse>
    
    @PUT("subscriptions/{id}/resume")
    suspend fun resumeSubscription(@Path("id") subscriptionId: String): Response<SubscriptionResponse>
    
    @PUT("subscriptions/{id}/cancel")
    suspend fun cancelSubscription(@Path("id") subscriptionId: String): Response<SubscriptionResponse>
    
    @PUT("subscriptions/{id}/upgrade")
    suspend fun upgradeSubscription(
        @Path("id") subscriptionId: String,
        @Body request: SubscriptionRequest
    ): Response<SubscriptionResponse>
    
    // Payments
    @POST("payments/process")
    suspend fun processPayment(@Body request: PaymentRequest): Response<PaymentResponse>
    
    @GET("payments/history")
    suspend fun getPaymentHistory(@Query("page") page: Int): Response<List<PaymentResponse>>
    
    @POST("payments/setup-recurring")
    suspend fun setupRecurringPayment(@Body request: PaymentRequest): Response<PaymentResponse>
    
    @PUT("payments/{id}/refund")
    suspend fun refundPayment(@Path("id") paymentId: String): Response<PaymentResponse>
    
    // Inventory
    @GET("inventory")
    suspend fun getInventory(@Query("page") page: Int): Response<List<InventoryResponse>>
    
    @POST("inventory/allocate")
    suspend fun allocateInventory(@Body request: InventoryRequest): Response<InventoryResponse>
    
    @PUT("inventory/{id}")
    suspend fun updateInventory(
        @Path("id") inventoryId: String,
        @Body request: InventoryRequest
    ): Response<InventoryResponse>
    
    // Shipment
    @GET("shipments")
    suspend fun getShipments(@Query("page") page: Int): Response<List<ShipmentResponse>>
    
    @GET("shipments/{id}")
    suspend fun getShipment(@Path("id") shipmentId: String): Response<ShipmentResponse>
    
    @POST("shipments/generate-manifest")
    suspend fun generateShipmentManifest(): Response<ShipmentResponse>
    
    @PUT("shipments/{id}/status")
    suspend fun updateShipmentStatus(
        @Path("id") shipmentId: String,
        @Body status: Map<String, String>
    ): Response<ShipmentResponse>
    
    // Coupons
    @GET("coupons")
    suspend fun getCoupons(): Response<List<CouponResponse>>
    
    @POST("coupons/validate")
    suspend fun validateCoupon(@Body code: Map<String, String>): Response<CouponResponse>
    
    @POST("coupons")
    suspend fun createCoupon(@Body request: CouponRequest): Response<CouponResponse>
    
    // Reports
    @GET("reports/churn-rate")
    suspend fun getChurnRate(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<ReportResponse>
    
    @GET("reports/revenue")
    suspend fun getRevenueReport(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<ReportResponse>
    
    @GET("reports/active-subscribers")
    suspend fun getActiveSubscribers(): Response<ReportResponse>
    
    @GET("reports/analytics")
    suspend fun getAnalytics(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<ReportResponse>
    
    // Notifications
    @PUT("notifications/fcm-token")
    suspend fun updateFcmToken(@Body fcmToken: Map<String, String>): Response<Map<String, Boolean>>
}
