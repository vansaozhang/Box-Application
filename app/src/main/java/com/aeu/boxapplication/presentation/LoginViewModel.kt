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
                    body?.let {
                        // This saves everything so the Profile screen can read it
                        sessionManager.saveUserDetail(
                            name = it.user?.name ?: "User",
                            email = it.user?.email ?: "",
                            token = it.accessToken
                        )
                    }

                    if (body?.accessToken != null) {
                        val userName = body.user?.name ?: "User"
                        onSuccess(userName)
                    } else {
                        // FIXED TYPO HERE: Changed errormessage to errorMessage
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