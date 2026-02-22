package com.aeu.boxapplication.data.remote

import com.aeu.boxapplication.core.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://subscription-backend-528466251837.us-central1.run.app/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // In RetrofitClient.kt
    val authApiService: AuthApiService by lazy { // Ensure this is AuthApiService
        retrofit.create(AuthApiService::class.java)
    }
}