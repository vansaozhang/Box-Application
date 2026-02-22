package com.aeu.boxapplication.data.remote

import com.aeu.boxapplication.data.remote.dto.response.AuthResponse
import com.aeu.boxapplication.data.remote.dto.request.LoginRequest
import com.aeu.boxapplication.data.remote.dto.request.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// 2. Data you get back (Already defined)
data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val userName: String? // Added this to save the name in session
)

// 3. Updated API Endpoints
interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // --- ADDED LOGIN ENDPOINT ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}