package com.aeu.boxapplication.domain.repository

import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.dto.request.RegisterRequest
import com.aeu.boxapplication.data.remote.dto.response.AuthResponse
import org.json.JSONObject

class AuthRepository(private val api: AuthApiService) {
        suspend fun registerUser(username: String, email: String, password: String): Result<AuthResponse> {
            return try {
                val response = api.register(RegisterRequest(username, email, password))

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    // This parses the 400/500 error messages from the server
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    // Try to parse JSON error message if available
                    val parsedError = try {
                        val json = JSONObject(errorMsg)
                        json.optString("message", errorMsg)
                    } catch (e: Exception) {
                        errorMsg
                    }
                    Result.failure(Exception(parsedError))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

}