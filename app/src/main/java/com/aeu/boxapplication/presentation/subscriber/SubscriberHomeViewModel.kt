package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.dto.response.SubscriberDashboardResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

data class SubscriberHomeUiState(
    val isLoading: Boolean = false,
    val dashboard: SubscriberDashboardResponse? = null,
    val errorMessage: String? = null,
    val isAuthenticationError: Boolean = false
)

class SubscriberHomeViewModel(
    private val authService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf(SubscriberHomeUiState())
        private set
    private var loadedToken: String? = null

    fun loadDashboard(forceRefresh: Boolean = false) {
        if (uiState.isLoading && !forceRefresh) {
            return
        }

        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            uiState = uiState.copy(errorMessage = "Please login again to load your dashboard.")
            return
        }

        val tokenChanged = loadedToken != token
        if (!forceRefresh && !tokenChanged && uiState.dashboard != null) {
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = authService.getSubscriberDashboard("Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        loadedToken = token
                        uiState = SubscriberHomeUiState(dashboard = body)
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = "Dashboard data is unavailable right now."
                        )
                    }
                } else {
                    // Check if it's an authentication error (expired token)
                    if (response.code() == 401 || response.code() == 403) {
                        sessionManager.clearSession()
                        uiState = uiState.copy(
                            isLoading = false,
                            isAuthenticationError = true,
                            errorMessage = "Your session has expired. Please login again."
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = extractServerMessage(response.errorBody()?.string())
                                ?: "Failed to load dashboard."
                        )
                    }
                }
            } catch (error: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = mapExceptionToMessage(error)
                )
            }
        }
    }

    fun reset() {
        loadedToken = null
        uiState = SubscriberHomeUiState()
    }

    fun dismissError() {
        uiState = uiState.copy(errorMessage = null)
    }

    private fun mapExceptionToMessage(error: Exception): String {
        return when (error) {
            is SocketTimeoutException -> "Dashboard request timed out. Please try again."
            is IOException -> "No internet connection. Please reconnect and retry."
            else -> "Something unexpected happened while loading the dashboard."
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
                json.optJSONObject("error")?.optString("message")
            ).firstOrNull { !it.isNullOrBlank() && it != "null" }
        }.getOrNull()
    }
}
