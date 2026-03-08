package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeu.boxapplication.core.utils.CurrencyUtils
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.AuthApiService
import com.aeu.boxapplication.data.remote.dto.response.SubscriptionPlanFrequencyOptionResponse
import com.aeu.boxapplication.data.remote.dto.response.SubscriptionPlanStorefrontItemResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Locale

enum class ShopCatalogSort(val label: String) {
    Rating("Sort by ★"),
    Price("Sort by $")
}

data class ShopPlanUiModel(
    val id: String,
    val sourcePlanName: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val price: Double,
    val priceLabel: String,
    val periodLabel: String,
    val badge: String?,
    val featuredLabel: String?,
    val rating: Double,
    val ratingLabel: String,
    val isFeatured: Boolean,
    val features: List<String>,
    val frequencyOptions: List<ShopPlanFrequencyOptionUiModel>
)

data class ShopPlanFrequencyOptionUiModel(
    val id: String,
    val label: String,
    val frequencyInDays: Int,
    val price: Double,
    val priceLabel: String,
    val periodLabel: String
)

data class ShopProductsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val sort: ShopCatalogSort = ShopCatalogSort.Rating,
    val categories: List<String> = listOf("All", "Beauty", "Tech", "Snacks", "Wellness"),
    val featuredPlan: ShopPlanUiModel? = null,
    val plans: List<ShopPlanUiModel> = emptyList()
)

class ShopProductsViewModel(
    private val authService: AuthApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf(ShopProductsUiState())
        private set

    private var allPlans: List<ShopPlanUiModel> = emptyList()
    private var preferredFeaturedPlanId: String? = null
    private var seedAttempted = false
    private var loadedToken: String? = null

    fun loadStorefront(forceRefresh: Boolean = false) {
        if (uiState.isLoading || uiState.isRefreshing) {
            return
        }

        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            uiState = uiState.copy(errorMessage = "Please login again to load the package catalog.")
            return
        }

        val tokenChanged = loadedToken != token
        if (tokenChanged) {
            allPlans = emptyList()
            preferredFeaturedPlanId = null
            seedAttempted = false
            uiState = ShopProductsUiState()
        }

        if (!forceRefresh && !tokenChanged && (uiState.featuredPlan != null || uiState.plans.isNotEmpty())) {
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = !forceRefresh && allPlans.isEmpty(),
                isRefreshing = forceRefresh && allPlans.isNotEmpty(),
                errorMessage = null
            )

            try {
                val response = authService.getSubscriptionPlanStorefront("Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()
                    val storefrontPlans = listOfNotNull(body?.featuredPlan) + body?.plans.orEmpty()

                    if (body != null && storefrontPlans.isNotEmpty()) {
                        loadedToken = token
                        allPlans = storefrontPlans.map { it.toUiModel() }
                        preferredFeaturedPlanId = body.featuredPlan?.id
                        val categories = body.categories.ifEmpty {
                            listOf("All", "Beauty", "Tech", "Snacks", "Wellness")
                        }
                        publishFilteredState(categories = categories)
                    } else if (!seedAttempted) {
                        seedAttempted = true
                        seedDefaultsAndReload(token)
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = "No packages are available right now."
                        )
                    }
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = extractServerMessage(response.errorBody()?.string())
                            ?: "Failed to load package catalog."
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

    fun updateSearchQuery(query: String) {
        uiState = uiState.copy(searchQuery = query)
        publishFilteredState()
    }

    fun selectCategory(category: String) {
        if (category == uiState.selectedCategory) {
            return
        }
        uiState = uiState.copy(selectedCategory = category)
        publishFilteredState()
    }

    fun toggleSort() {
        val nextSort = when (uiState.sort) {
            ShopCatalogSort.Rating -> ShopCatalogSort.Price
            ShopCatalogSort.Price -> ShopCatalogSort.Rating
        }
        uiState = uiState.copy(sort = nextSort)
        publishFilteredState()
    }

    fun reset() {
        loadedToken = null
        allPlans = emptyList()
        preferredFeaturedPlanId = null
        seedAttempted = false
        uiState = ShopProductsUiState()
    }

    fun dismissError() {
        uiState = uiState.copy(errorMessage = null)
    }

    private suspend fun seedDefaultsAndReload(token: String) {
        val seedResponse = authService.seedDefaultPlans("Bearer $token")
        if (!seedResponse.isSuccessful) {
            uiState = uiState.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessage = "Failed to seed package plans."
            )
            return
        }

        uiState = uiState.copy(isLoading = false, isRefreshing = false)
        loadStorefront(forceRefresh = true)
    }

    private fun publishFilteredState(categories: List<String> = uiState.categories) {
        val query = uiState.searchQuery.trim()
        val selectedCategory = uiState.selectedCategory

        val filteredPlans = allPlans.filter { plan ->
            val matchesCategory = selectedCategory == "All" || plan.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = query.isBlank() || buildSearchableText(plan).contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        val sortedPlans = when (uiState.sort) {
            ShopCatalogSort.Rating -> filteredPlans.sortedWith(
                compareByDescending<ShopPlanUiModel> { it.rating }
                    .thenBy { it.price }
            )

            ShopCatalogSort.Price -> filteredPlans.sortedBy { it.price }
        }

        val featuredPlan = sortedPlans.firstOrNull { it.id == preferredFeaturedPlanId }
            ?: sortedPlans.firstOrNull { it.isFeatured }
            ?: sortedPlans.firstOrNull()
        val discoverPlans = sortedPlans.filter { it.id != featuredPlan?.id }

        uiState = uiState.copy(
            isLoading = false,
            isRefreshing = false,
            categories = categories,
            featuredPlan = featuredPlan,
            plans = discoverPlans
        )
    }

    private fun buildSearchableText(plan: ShopPlanUiModel): String {
        return listOf(plan.title, plan.subtitle, plan.category, plan.sourcePlanName)
            .joinToString(" ")
            .lowercase(Locale.US)
    }

    private fun SubscriptionPlanStorefrontItemResponse.toUiModel(): ShopPlanUiModel {
        return ShopPlanUiModel(
            id = id,
            sourcePlanName = planName,
            title = title,
            subtitle = subtitle,
            category = category,
            price = price,
            priceLabel = CurrencyUtils.formatPrice(price),
            periodLabel = periodLabel,
            badge = badge,
            featuredLabel = featuredLabel,
            rating = rating,
            ratingLabel = String.format(Locale.US, "%.1f", rating),
            isFeatured = isFeatured,
            features = features.orEmpty(),
            frequencyOptions = frequencyOptions.orEmpty().map { it.toUiModel() }
        )
    }

    private fun SubscriptionPlanFrequencyOptionResponse.toUiModel(): ShopPlanFrequencyOptionUiModel {
        return ShopPlanFrequencyOptionUiModel(
            id = id,
            label = label,
            frequencyInDays = frequencyInDays,
            price = price,
            priceLabel = CurrencyUtils.formatPrice(price),
            periodLabel = periodLabel
        )
    }

    private fun mapExceptionToMessage(error: Exception): String {
        return when (error) {
            is SocketTimeoutException -> "Plan catalog request timed out. Please try again."
            is IOException -> "No internet connection. Please reconnect and retry."
            else -> "Something unexpected happened while loading plans."
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
