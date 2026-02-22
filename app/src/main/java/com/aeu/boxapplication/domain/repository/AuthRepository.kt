package com.aeu.boxapplication.domain.repository

import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.dto.request.RegisterRequest
import org.json.JSONObject

class AuthRepository(private val api: AuthApiService) {
        suspend fun registerUser(username: String, email: String, password: String): Result<String> {
            return try {
                val response = api.register(RegisterRequest(username, email, password))

                if (response.isSuccessful) {
                    Result.success("Success")
                } else {
                    // This parses the 400/500 error messages from the server
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

}