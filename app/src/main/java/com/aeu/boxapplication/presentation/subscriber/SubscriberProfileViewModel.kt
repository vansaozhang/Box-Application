package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.dto.request.CreateMyAddressRequest
import com.aeu.boxapplication.data.remote.dto.response.AuthMeResponse
import com.aeu.boxapplication.data.remote.dto.response.SubscriberAddressResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class SubscriberAddressUiModel(
    val id: String,
    val label: String,
    val address: String,
    val phone: String?,
    val isPrimary: Boolean
)

data class SubscriberProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSavingAddress: Boolean = false,
    val profile: AuthMeResponse? = null,
    val addresses: List<SubscriberAddressUiModel> = emptyList(),
    val errorMessage: String? = null
)

class SubscriberProfileViewModel(
    private val authService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf(SubscriberProfileUiState())
        private set

    private var loadedToken: String? = null
    private var remoteAddresses: List<SubscriberAddressResponse> = emptyList()

    fun loadProfile(forceRefresh: Boolean = false) {
        if (uiState.isLoading || uiState.isRefreshing || uiState.isSavingAddress) {
            return
        }

        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            uiState = uiState.copy(errorMessage = "Please login again to load your profile.")
            return
        }

        val tokenChanged = loadedToken != token
        if (tokenChanged) {
            remoteAddresses = emptyList()
            uiState = SubscriberProfileUiState()
        }

        if (!forceRefresh && !tokenChanged && uiState.profile != null) {
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = !forceRefresh && uiState.profile == null,
                isRefreshing = forceRefresh && uiState.profile != null,
                errorMessage = null
            )

            try {
                val meResponse = authService.getMe("Bearer $token")
                val addressesResponse = authService.getMyAddresses("Bearer $token")

                if (meResponse.isSuccessful && addressesResponse.isSuccessful) {
                    val profile = meResponse.body()
                    if (profile != null) {
                        loadedToken = token
                        remoteAddresses = addressesResponse.body().orEmpty()
                        uiState = uiState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            profile = profile,
                            addresses = buildAddressUiModels(remoteAddresses)
                        )
                        sessionManager.saveUserDetail(
                            name = profile.name?.takeIf { it.isNotBlank() }
                                ?: sessionManager.getUserName()
                                ?: profile.phoneNumber
                                ?: profile.email
                                ?: "Subscriber",
                            email = profile.email,
                            phone = profile.phoneNumber,
                            token = token
                        )
                    } else {
                        remoteAddresses = emptyList()
                        uiState = uiState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isSavingAddress = false,
                            addresses = buildAddressUiModels(remoteAddresses),
                            errorMessage = "Profile details are unavailable right now."
                        )
                    }
                } else {
                    val serverMessage = extractServerMessage(
                        meResponse.errorBody()?.string() ?: addressesResponse.errorBody()?.string()
                    )
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isSavingAddress = false,
                        addresses = buildAddressUiModels(remoteAddresses),
                        errorMessage = serverMessage ?: "Failed to load your profile."
                    )
                }
            } catch (error: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isSavingAddress = false,
                    addresses = buildAddressUiModels(remoteAddresses),
                    errorMessage = mapExceptionToMessage(error)
                )
            }
        }
    }

    fun reset() {
        loadedToken = null
        remoteAddresses = emptyList()
        uiState = SubscriberProfileUiState()
    }

    fun dismissError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun addShippingAddress(
        address: String,
        phone: String? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (uiState.isSavingAddress) {
            return
        }

        val normalizedAddress = address.trim()
        val normalizedPhone = (phone?.trim().takeUnless { it.isNullOrBlank() }
            ?: uiState.profile?.phoneNumber?.trim().orEmpty())
        val phoneDigits = normalizedPhone.filter { it.isDigit() }
        val token = sessionManager.getAuthToken()

        val validationError = when {
            token.isNullOrBlank() -> "Please login again to save an address."
            normalizedAddress.length < 8 -> "Enter a full delivery address."
            phoneDigits.length !in 8..15 -> "Enter a valid phone number."
            else -> null
        }

        if (validationError != null) {
            onError(validationError)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSavingAddress = true, errorMessage = null)
            try {
                val response = authService.createMyAddress(
                    authHeader = "Bearer $token",
                    request = CreateMyAddressRequest(
                        phone = normalizedPhone,
                        address = normalizedAddress
                    )
                )

                if (response.isSuccessful) {
                    response.body()?.let { createdAddress ->
                        remoteAddresses = (listOf(createdAddress) + remoteAddresses)
                            .distinctBy { it.id }
                    }
                    uiState = uiState.copy(
                        isSavingAddress = false,
                        addresses = buildAddressUiModels(remoteAddresses),
                        errorMessage = null
                    )
                    onSuccess()
                } else {
                    val serverMessage = extractServerMessage(response.errorBody()?.string())
                        ?: "Failed to save your address."
                    uiState = uiState.copy(
                        isSavingAddress = false,
                        errorMessage = serverMessage
                    )
                    onError(serverMessage)
                }
            } catch (error: Exception) {
                val message = mapExceptionToMessage(error)
                uiState = uiState.copy(
                    isSavingAddress = false,
                    errorMessage = message
                )
                onError(message)
            }
        }
    }

    fun memberSinceLabel(): String {
        val createdAt = uiState.profile?.createdAt ?: return "Member"
        return runCatching {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(createdAt)
            if (date != null) {
                "Member since ${SimpleDateFormat("MMM yyyy", Locale.US).format(date)}"
            } else {
                "Member"
            }
        }.getOrDefault("Member")
    }

    private fun mapExceptionToMessage(error: Exception): String {
        return when (error) {
            is SocketTimeoutException -> "Request timed out. Please try again."
            is IOException -> "No internet connection. Please reconnect and retry."
            else -> "Something unexpected happened while processing your profile."
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

    private fun buildAddressUiModels(
        remoteAddresses: List<SubscriberAddressResponse>
    ): List<SubscriberAddressUiModel> {
        return remoteAddresses
            .sortedByDescending { it.createdAt.orEmpty() }
            .map { it.toUiModel() }
            .distinctBy(::addressDedupKey)
            .mapIndexed { index, address ->
                address.copy(
                    label = if (index == 0) "Most Recent Address" else "Saved Address ${index + 1}",
                    isPrimary = index == 0
                )
            }
    }

    private fun SubscriberAddressResponse.toUiModel(): SubscriberAddressUiModel {
        return SubscriberAddressUiModel(
            id = id,
            label = "",
            address = address,
            phone = phone,
            isPrimary = false
        )
    }

    private fun addressDedupKey(address: SubscriberAddressUiModel): String {
        return buildString {
            append(address.address.trim().lowercase())
            append("|")
            append(address.phone.orEmpty().filter { it.isDigit() })
        }
    }
}
