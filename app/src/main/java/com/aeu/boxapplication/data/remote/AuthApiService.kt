package com.aeu.boxapplication.data.remote

import com.aeu.boxapplication.data.remote.dto.response.AuthResponse
import com.aeu.boxapplication.data.remote.dto.request.LoginRequest
import com.aeu.boxapplication.data.remote.dto.request.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}