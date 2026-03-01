package com.aeu.boxapplication.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.dto.request.LoginRequest
import com.aeu.boxapplication.data.remote.dto.response.AuthResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val authService: AuthApiService,private val sessionManager: SessionManager) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun performLogin(email: String, password: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val loginRequest = LoginRequest(
                    email = email.trim(),
                    password = password.trim()
                )
                val response = authService.login(loginRequest)

                if (response.isSuccessful) {
                    val body: AuthResponse? = response.body()
                    val accessToken = body?.accessToken

                    if (!accessToken.isNullOrBlank()) {
                        val meResponse = authService.getMe("Bearer $accessToken")
                        if (meResponse.isSuccessful) {
                            val meBody = meResponse.body()
                            if (meBody != null) {
                                val userName = body?.user?.name?.takeIf { it.isNotBlank() }
                                    ?: sessionManager.getUserName().takeUnless { it.isNullOrBlank() }
                                    ?: meBody.email.substringBefore("@")

                                sessionManager.saveUserDetail(
                                    name = userName,
                                    email = meBody.email,
                                    token = accessToken
                                )
                                sessionManager.setHasAccount()
                                onSuccess(userName)
                            } else {
                                errorMessage = "Login failed to load profile"
                            }
                        } else {
                            val errorJson = meResponse.errorBody()?.string()
                            errorMessage = "Login failed to validate account"
                            println("API_ME_ERROR: $errorJson")
                        }
                    } else {
                        errorMessage = "Login successful but no user data found"
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    errorMessage = "Invalid email or password"
                    println("API_ERROR: $errorJson")
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
