package com.aeu.boxapplication.data.remote

import com.aeu.boxapplication.data.remote.dto.response.SubscriptionApiResponse
import com.google.gson.Gson

data class SubscriptionLookupResult(
    val code: Int,
    val subscription: SubscriptionApiResponse?,
    val errorBody: String? = null,
    val parseError: String? = null
) {
    val isSuccessful: Boolean
        get() = code in 200..299 && parseError == null

    val hasActiveSubscription: Boolean
        get() = subscription?.status.equals("ACTIVE", ignoreCase = true)
}

private val subscriptionLookupGson = Gson()

suspend fun AuthApiService.getMySubscriptionSafely(
    authHeader: String
): SubscriptionLookupResult {
    val response = getMySubscription(authHeader)
    val rawBody = response.body()?.string()?.trim()

    val subscription = when {
        rawBody.isNullOrBlank() || rawBody == "null" -> null
        else -> runCatching {
            subscriptionLookupGson.fromJson(rawBody, SubscriptionApiResponse::class.java)
        }.getOrElse { error ->
            return SubscriptionLookupResult(
                code = response.code(),
                subscription = null,
                parseError = error.localizedMessage ?: "Failed to parse subscription response"
            )
        }
    }

    return SubscriptionLookupResult(
        code = response.code(),
        subscription = subscription,
        errorBody = if (response.isSuccessful) null else response.errorBody()?.string()
    )
}
