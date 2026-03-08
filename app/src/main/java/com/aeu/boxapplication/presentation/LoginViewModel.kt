package com.aeu.boxapplication.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.getMySubscriptionSafely
import com.aeu.boxapplication.data.remote.dto.request.LoginRequest
import com.aeu.boxapplication.data.remote.dto.response.AuthResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

enum class PostLoginDestination {
    HOME,
    SUBSCRIPTIONS_EMPTY
}

data class LoginUiMessage(
    val title: String,
    val message: String
)

class LoginViewModel(private val authService: AuthApiService,private val sessionManager: SessionManager) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var uiMessage by mutableStateOf<LoginUiMessage?>(null)
        private set

    fun clearUiMessage() {
        uiMessage = null
    }

    fun performLogin(
        email: String,
        password: String,
        onSuccess: (String, PostLoginDestination) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            clearUiMessage()
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

                                val subscriptionResponse = authService.getMySubscriptionSafely("Bearer $accessToken")
                                if (
                                    subscriptionResponse.isSuccessful ||
                                    subscriptionResponse.code == 404
                                ) {
                                    val hasActiveSubscription = subscriptionResponse.hasActiveSubscription

                                    sessionManager.saveUserDetail(
                                        name = userName,
                                        email = meBody.email,
                                        token = accessToken
                                    )
                                    sessionManager.setHasAccount()
                                    onSuccess(
                                        userName,
                                        if (hasActiveSubscription) {
                                            PostLoginDestination.HOME
                                        } else {
                                            PostLoginDestination.SUBSCRIPTIONS_EMPTY
                                        }
                                    )
                                } else {
                                    val subscriptionError = subscriptionResponse.parseError
                                        ?: subscriptionResponse.errorBody
                                    showUiMessage(
                                        title = "Couldn't finish sign in",
                                        message = "Your account was verified, but we couldn't confirm the subscription status. Please try again."
                                    )
                                    println("API_SUBSCRIPTION_ERROR: $subscriptionError")
                                }
                            } else {
                                showUiMessage(
                                    title = "Couldn't finish sign in",
                                    message = "We signed you in, but your profile details did not load. Please try again."
                                )
                            }
                        } else {
                            val errorJson = meResponse.errorBody()?.string()
                            showUiMessage(
                                title = "Couldn't finish sign in",
                                message = "We couldn't validate your account details right now. Please try again."
                            )
                            println("API_ME_ERROR: $errorJson")
                        }
                    } else {
                        showUiMessage(
                            title = "Unexpected sign-in response",
                            message = "The server did not return the account details needed to continue. Please try again."
                        )
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    handleLoginFailure(response.code(), errorJson)
                    println("API_ERROR: $errorJson")
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun showUiMessage(title: String, message: String) {
        uiMessage = LoginUiMessage(title = title, message = message)
    }

    private fun handleLoginFailure(code: Int, errorBody: String?) {
        val serverMessage = extractServerMessage(errorBody)
        when (code) {
            400, 401 -> showUiMessage(
                title = "Incorrect email or password",
                message = "Check your login details and try again."
            )

            403 -> showUiMessage(
                title = "Account access restricted",
                message = serverMessage ?: "This account cannot sign in right now. Contact support if the issue continues."
            )

            429 -> showUiMessage(
                title = "Too many attempts",
                message = serverMessage ?: "Please wait a moment before trying to sign in again."
            )

            in 500..599 -> showUiMessage(
                title = "Server unavailable",
                message = "We couldn't sign you in because the server is having trouble. Please try again shortly."
            )

            else -> showUiMessage(
                title = "Couldn't sign in",
                message = serverMessage ?: "Something went wrong while signing you in. Please try again."
            )
        }
    }

    private fun handleException(error: Exception) {
        when (error) {
            is SocketTimeoutException -> showUiMessage(
                title = "Request timed out",
                message = "The sign-in request took too long. Check your connection and try again."
            )

            is IOException -> showUiMessage(
                title = "No internet connection",
                message = "Connect to the internet and try signing in again."
            )

            else -> showUiMessage(
                title = "Couldn't sign in",
                message = "Something unexpected happened. Please try again."
            )
        }
    }

    private fun extractServerMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) {
            return null
        }

        return runCatching {
            val json = JSONObject(errorBody)
            sequenceOf(
                json.optString("message"),
                json.optString("error"),
                json.optJSONObject("error")?.optString("message"),
                json.optJSONObject("errors")?.optString("message")
            ).firstOrNull { !it.isNullOrBlank() && it != "null" }
        }.getOrNull()
    }
}
