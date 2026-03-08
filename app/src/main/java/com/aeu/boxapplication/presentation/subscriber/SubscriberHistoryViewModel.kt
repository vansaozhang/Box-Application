package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeu.boxapplication.core.utils.CurrencyUtils
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.dto.response.SubscriberShipmentHistoryDetailResponse
import com.aeu.boxapplication.data.remote.dto.response.SubscriberShipmentHistoryItemResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class ShipmentHistoryUiModel(
    val id: String,
    val title: String,
    val dateLabel: String,
    val status: String,
    val statusLabel: String,
    val trackingLabel: String?,
    val amountLabel: String
)

data class ShipmentHistoryDetailUiModel(
    val id: String,
    val title: String,
    val shipmentDateLabel: String,
    val statusLabel: String,
    val trackingLabel: String?,
    val amountLabel: String,
    val paymentStatusLabel: String,
    val paymentDateLabel: String?,
    val subscriptionStatusLabel: String,
    val subscriptionPeriodLabel: String,
    val renewalWindowLabel: String?,
    val contactName: String,
    val addressLabel: String,
    val phoneLabel: String?
)

data class SubscriberHistoryUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isDetailLoading: Boolean = false,
    val history: List<ShipmentHistoryUiModel> = emptyList(),
    val selectedDetail: ShipmentHistoryDetailUiModel? = null,
    val errorMessage: String? = null
)

class SubscriberHistoryViewModel(
    private val authService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by androidx.compose.runtime.mutableStateOf(SubscriberHistoryUiState())
        private set

    private var loadedToken: String? = null
    private val detailCache = mutableMapOf<String, ShipmentHistoryDetailUiModel>()

    fun loadHistory(forceRefresh: Boolean = false) {
        if (uiState.isLoading || uiState.isRefreshing) {
            return
        }

        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            uiState = uiState.copy(errorMessage = "Please login again to load your shipment history.")
            return
        }

        val tokenChanged = loadedToken != token
        if (tokenChanged) {
            detailCache.clear()
            uiState = SubscriberHistoryUiState()
        }

        if (!forceRefresh && !tokenChanged && uiState.history.isNotEmpty()) {
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = !forceRefresh && uiState.history.isEmpty(),
                isRefreshing = forceRefresh && uiState.history.isNotEmpty(),
                errorMessage = null
            )

            try {
                val response = authService.getShipmentHistory("Bearer $token")
                if (response.isSuccessful) {
                    loadedToken = token
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        history = response.body().orEmpty().map { it.toUiModel() }
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = extractServerMessage(response.errorBody()?.string())
                            ?: "Failed to load shipment history."
                    )
                }
            } catch (error: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = mapExceptionToMessage(error)
                )
            }
        }
    }

    fun loadDetail(shipmentId: String, forceRefresh: Boolean = false) {
        if (uiState.isDetailLoading) {
            return
        }

        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            uiState = uiState.copy(errorMessage = "Please login again to load this shipment.")
            return
        }

        if (!forceRefresh) {
            detailCache[shipmentId]?.let { cached ->
                uiState = uiState.copy(selectedDetail = cached)
                return
            }
        }

        viewModelScope.launch {
            uiState = uiState.copy(isDetailLoading = true, errorMessage = null)
            try {
                val response = authService.getShipmentHistoryDetail("Bearer $token", shipmentId)
                if (response.isSuccessful) {
                    val detail = response.body()?.toUiModel()
                    if (detail != null) {
                        detailCache[shipmentId] = detail
                        uiState = uiState.copy(
                            isDetailLoading = false,
                            selectedDetail = detail
                        )
                    } else {
                        uiState = uiState.copy(
                            isDetailLoading = false,
                            errorMessage = "Shipment detail is unavailable right now."
                        )
                    }
                } else {
                    uiState = uiState.copy(
                        isDetailLoading = false,
                        errorMessage = extractServerMessage(response.errorBody()?.string())
                            ?: "Failed to load shipment detail."
                    )
                }
            } catch (error: Exception) {
                uiState = uiState.copy(
                    isDetailLoading = false,
                    errorMessage = mapExceptionToMessage(error)
                )
            }
        }
    }

    fun reset() {
        loadedToken = null
        detailCache.clear()
        uiState = SubscriberHistoryUiState()
    }

    fun dismissError() {
        uiState = uiState.copy(errorMessage = null)
    }

    private fun SubscriberShipmentHistoryItemResponse.toUiModel(): ShipmentHistoryUiModel {
        return ShipmentHistoryUiModel(
            id = id,
            title = planName,
            dateLabel = shipmentDate.toReadableDate(),
            status = status,
            statusLabel = status.toReadableStatus(),
            trackingLabel = trackingNumber?.takeIf { it.isNotBlank() }?.let { "Tracking $it" },
            amountLabel = CurrencyUtils.formatCurrency(amount, currency)
        )
    }

    private fun SubscriberShipmentHistoryDetailResponse.toUiModel(): ShipmentHistoryDetailUiModel {
        return ShipmentHistoryDetailUiModel(
            id = id,
            title = planName,
            shipmentDateLabel = shipmentDate.toReadableDate(),
            statusLabel = status.toReadableStatus(),
            trackingLabel = trackingNumber?.takeIf { it.isNotBlank() },
            amountLabel = CurrencyUtils.formatPrice(amount),
            paymentStatusLabel = paymentStatus.toReadableStatus(),
            paymentDateLabel = paymentDate.toReadableDateTime(),
            subscriptionStatusLabel = subscriptionStatus.toReadableStatus(),
            subscriptionPeriodLabel = periodLabel,
            renewalWindowLabel = buildRenewalWindow(
                startDate = subscriptionStartDate,
                endDate = subscriptionEndDate
            ),
            contactName = shippingAddress.contactName,
            addressLabel = shippingAddress.address ?: "No shipping address on file.",
            phoneLabel = shippingAddress.phone
        )
    }

    private fun buildRenewalWindow(startDate: String?, endDate: String?): String? {
        val startLabel = startDate.toReadableDateOrNull()
        val endLabel = endDate.toReadableDateOrNull()
        return when {
            startLabel == null && endLabel == null -> null
            startLabel != null && endLabel != null -> "$startLabel to $endLabel"
            startLabel != null -> "Started $startLabel"
            else -> "Renews $endLabel"
        }
    }

    private fun String?.toReadableDate(): String {
        return toReadableDateOrNull() ?: "Unavailable"
    }

    private fun String?.toReadableDateOrNull(): String? {
        if (this.isNullOrBlank()) {
            return null
        }

        val inputPatterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd"
        )

        inputPatterns.forEach { pattern ->
            runCatching {
                val parser = SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = parser.parse(this)
                if (date != null) {
                    return SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date)
                }
            }
        }

        return this
    }

    private fun String?.toReadableDateTime(): String? {
        if (this.isNullOrBlank()) {
            return null
        }

        return runCatching {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(this)
            if (date != null) {
                SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US).format(date)
            } else {
                this
            }
        }.getOrElse { this }
    }

    private fun String.toReadableStatus(): String {
        return lowercase(Locale.US)
            .split('_', ' ')
            .filter { it.isNotBlank() }
            .joinToString(" ") { part -> part.replaceFirstChar { it.titlecase(Locale.US) } }
    }

    private fun mapExceptionToMessage(error: Exception): String {
        return when (error) {
            is SocketTimeoutException -> "Shipment history request timed out. Please try again."
            is IOException -> "No internet connection. Please reconnect and retry."
            else -> "Something unexpected happened while loading shipment history."
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
