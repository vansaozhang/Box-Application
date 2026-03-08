package com.aeu.boxapplication
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aeu.boxapplication.core.utils.PendingPlanOption
import com.aeu.boxapplication.core.utils.PendingPlanSelection
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.RetrofitClient
import com.aeu.boxapplication.data.remote.getMySubscriptionSafely
import com.aeu.boxapplication.domain.repository.AuthRepository
import com.aeu.boxapplication.presentation.LoginViewModel
import com.aeu.boxapplication.presentation.PostLoginDestination
import com.aeu.boxapplication.presentation.auth.LoginScreen
import com.aeu.boxapplication.presentation.auth.RegisterScreen
import com.aeu.boxapplication.presentation.auth.RegisterViewModel
import com.aeu.boxapplication.presentation.auth.RegisterViewModelFactory
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.presentation.onboarding.LoadingScreen
import com.aeu.boxapplication.presentation.payment.PaymentCardInput
import com.aeu.boxapplication.presentation.payment.PaymentConfirmationScreen
import com.aeu.boxapplication.presentation.payment.PaymentDetailsScreen
import com.aeu.boxapplication.presentation.profile.AddShippingAddressScreen
import com.aeu.boxapplication.presentation.profile.hasLocationPermission
import com.aeu.boxapplication.presentation.profile.resolveCurrentAddress
import com.aeu.boxapplication.presentation.profile.ShippingAddressScreen
import com.aeu.boxapplication.presentation.subscription.BillingFrequencyOptionUi
import com.aeu.boxapplication.presentation.subscription.ConfirmSubscriptionScreen
import com.aeu.boxapplication.presentation.subscription.SubscriptionsEmptyScreen
import com.aeu.boxapplication.presentation.subscription.SubscriptionViewModel
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppLoadingHost
import com.aeu.boxapplication.presentation.subscriber.*
import com.aeu.boxapplication.ui.components.LocalAppLoadingHostState
import com.aeu.boxapplication.ui.components.AppNotificationHost
import com.aeu.boxapplication.ui.components.LocalAppNotificationHostState
import com.aeu.boxapplication.ui.components.rememberAppLoadingHostState
import com.aeu.boxapplication.ui.components.rememberAppNotificationHostState
import com.stripe.android.model.Address
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.ConfirmSetupIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.payments.paymentlauncher.rememberPaymentLauncher
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.White.toArgb(), Color.White.toArgb()),
            navigationBarStyle = SystemBarStyle.light(Color.White.toArgb(), Color.White.toArgb())
        )
        window.isNavigationBarContrastEnforced = false
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val sessionManager = remember { SessionManager.getInstance(context) }
            val notificationHostState = rememberAppNotificationHostState()
            val loadingHostState = rememberAppLoadingHostState()
            val authToken = sessionManager.getAuthToken()
            val sharedSubscriberHomeViewModel = remember(sessionManager) {
                ViewModelProvider(
                    this@MainActivity,
                    object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return SubscriberHomeViewModel(
                                authService = RetrofitClient.authApiService,
                                sessionManager = sessionManager
                            ) as T
                        }
                    }
                )[SubscriberHomeViewModel::class.java]
            }
            val sharedShopProductsViewModel = remember(sessionManager) {
                ViewModelProvider(
                    this@MainActivity,
                    object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return ShopProductsViewModel(
                                authService = RetrofitClient.authApiService,
                                sessionManager = sessionManager
                            ) as T
                        }
                    }
                )[ShopProductsViewModel::class.java]
            }
            val sharedHistoryViewModel = remember(sessionManager) {
                ViewModelProvider(
                    this@MainActivity,
                    object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return SubscriberHistoryViewModel(
                                authService = RetrofitClient.authApiService,
                                sessionManager = sessionManager
                            ) as T
                        }
                    }
                )[SubscriberHistoryViewModel::class.java]
            }
            val sharedProfileViewModel = remember(sessionManager) {
                ViewModelProvider(
                    this@MainActivity,
                    object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return SubscriberProfileViewModel(
                                authService = RetrofitClient.authApiService,
                                sessionManager = sessionManager
                            ) as T
                        }
                    }
                )[SubscriberProfileViewModel::class.java]
            }
            var hasHandledAutoAddressPrompt by remember(authToken) { mutableStateOf(false) }
            var isAutoCreatingDefaultAddress by remember(authToken) { mutableStateOf(false) }
            val profileUiState = sharedProfileViewModel.uiState
            val activeSubscriptionCount = sharedSubscriberHomeViewModel.uiState.dashboard
                ?.activeSubscriptions
                ?.size
                ?: 0
            val autoAddressScope = rememberCoroutineScope()

            fun createDefaultAddressFromCurrentLocation() {
                if (isAutoCreatingDefaultAddress || profileUiState.isSavingAddress) {
                    return
                }

                autoAddressScope.launch {
                    isAutoCreatingDefaultAddress = true
                    val result = resolveCurrentAddress(context)
                    isAutoCreatingDefaultAddress = false

                    result.fold(
                        onSuccess = { resolvedAddress ->
                            sharedProfileViewModel.addShippingAddress(
                                address = resolvedAddress,
                                phone = profileUiState.profile?.phoneNumber,
                                onSuccess = {},
                                onError = {}
                            )
                        },
                        onFailure = {}
                    )
                }
            }

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                if (result.values.any { it }) {
                    createDefaultAddressFromCurrentLocation()
                }
            }
            val shouldPreloadSubscriberTabs = !authToken.isNullOrBlank() &&
                    currentRoute != Screen.Loading.route &&
                    currentRoute != Screen.Login.route &&
                    currentRoute != Screen.Register.route
            val shouldOfferAutoAddressPrompt = !authToken.isNullOrBlank() &&
                    currentRoute?.startsWith(Screen.SubscriberHome.route) == true

            DisposableEffect(lifecycleOwner, authToken) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START && !authToken.isNullOrBlank()) {
                        hasHandledAutoAddressPrompt = false
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            LaunchedEffect(authToken, shouldPreloadSubscriberTabs) {
                if (authToken.isNullOrBlank()) {
                    sharedSubscriberHomeViewModel.reset()
                    sharedShopProductsViewModel.reset()
                    sharedHistoryViewModel.reset()
                    sharedProfileViewModel.reset()
                } else if (shouldPreloadSubscriberTabs) {
                    sharedSubscriberHomeViewModel.loadDashboard()
                    sharedShopProductsViewModel.loadStorefront()
                    sharedHistoryViewModel.loadHistory()
                    sharedProfileViewModel.loadProfile()
                }
            }

            LaunchedEffect(
                authToken,
                currentRoute,
                hasHandledAutoAddressPrompt,
                profileUiState.profile?.id,
                profileUiState.profile?.phoneNumber,
                profileUiState.addresses.size,
                profileUiState.isLoading,
                profileUiState.isRefreshing,
                profileUiState.isSavingAddress,
                profileUiState.errorMessage
            ) {
                if (authToken.isNullOrBlank()) {
                    return@LaunchedEffect
                }

                if (
                    !shouldOfferAutoAddressPrompt ||
                    hasHandledAutoAddressPrompt ||
                    profileUiState.isLoading ||
                    profileUiState.isRefreshing ||
                    profileUiState.isSavingAddress ||
                    profileUiState.errorMessage != null
                ) {
                    return@LaunchedEffect
                }

                val profile = profileUiState.profile ?: return@LaunchedEffect
                if (profileUiState.addresses.isNotEmpty()) {
                    hasHandledAutoAddressPrompt = true
                    return@LaunchedEffect
                }

                if (profile.phoneNumber.isNullOrBlank()) {
                    hasHandledAutoAddressPrompt = true
                    return@LaunchedEffect
                }

                hasHandledAutoAddressPrompt = true
                if (hasLocationPermission(context)) {
                    createDefaultAddressFromCurrentLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            // Define which screens should show the Bottom Navigation Bar
            val showBottomBar = currentRoute?.startsWith(Screen.SubscriberHome.route) == true ||
                    currentRoute == Screen.OrderHistory.route ||
                    currentRoute == Screen.ShopProducts.route ||
                    currentRoute == Screen.Profile.route

            CompositionLocalProvider(
                LocalAppNotificationHostState provides notificationHostState,
                LocalAppLoadingHostState provides loadingHostState
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppGlobalLoadingEffect(isVisible = isAutoCreatingDefaultAddress)
                    Scaffold(
                        containerColor = Color.White,
                        bottomBar = {
                            if (showBottomBar) {
                                SubscriberBottomNav(
                                    selected = when {
                                        currentRoute?.startsWith(Screen.SubscriberHome.route) == true -> SubscriberBottomNavItem.Home
                                        currentRoute == Screen.OrderHistory.route -> SubscriberBottomNavItem.History
                                        currentRoute == Screen.ShopProducts.route -> SubscriberBottomNavItem.Package
                                        currentRoute == Screen.Profile.route -> SubscriberBottomNavItem.Profile
                                        else -> SubscriberBottomNavItem.Home
                                    },
                                    onHomeClick = {
                                        navController.navigateToBottomTab(Screen.SubscriberHome.route)
                                    },
                                    onHistoryClick = {
                                        navController.navigateToBottomTab(Screen.OrderHistory.route)
                                    },
                                    onShopClick = {
                                        navController.navigateToBottomTab(Screen.ShopProducts.route)
                                    },
                                    onProfileClick = {
                                        navController.navigateToBottomTab(Screen.Profile.route)
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Loading.route, // App starts with Loading
                            modifier = Modifier.padding(innerPadding)
                        ) {
                    // 1. Splash/Loading Screen
                    composable(Screen.Loading.route) {
                        val loadingScope = rememberCoroutineScope()
                        LoadingScreen(
                            onFinished = {
                                loadingScope.launch {
                                    val navigateToHome: () -> Unit = {
                                        navController.navigate(Screen.SubscriberHome.route) {
                                            popUpTo(Screen.Loading.route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                    val navigateToSubscriptionsEmpty: () -> Unit = {
                                        navController.navigate(Screen.SubscriptionsEmpty.route) {
                                            popUpTo(Screen.Loading.route) { inclusive = true }
                                        }
                                    }

                                    val savedToken = sessionManager.getAuthToken()
                                    val savedName = sessionManager.getUserName()

                                    if (!savedToken.isNullOrBlank()) {
                                        try {
                                            val meResponse = RetrofitClient.authApiService.getMe("Bearer $savedToken")
                                            val meBody = meResponse.body()

                                            if (meResponse.isSuccessful && meBody != null) {
                                                val resolvedName = savedName
                                                    .takeUnless { it.isNullOrBlank() }
                                                    ?: resolveSubscriberDisplayName(
                                                        name = meBody.name,
                                                        phone = meBody.phoneNumber,
                                                        email = meBody.email
                                                    )

                                                val subscriptionResponse = RetrofitClient.authApiService
                                                    .getMySubscriptionSafely("Bearer $savedToken")
                                                if (
                                                    subscriptionResponse.isSuccessful ||
                                                    subscriptionResponse.code == 404
                                                ) {
                                                    sessionManager.saveUserDetail(
                                                        name = resolvedName,
                                                        email = meBody.email,
                                                        phone = meBody.phoneNumber,
                                                        token = savedToken
                                                    )
                                                    sessionManager.setHasAccount()

                                                    val hasActiveSubscription = subscriptionResponse
                                                        .hasActiveSubscription
                                                    if (hasActiveSubscription) {
                                                        navigateToHome()
                                                    } else {
                                                        navigateToSubscriptionsEmpty()
                                                    }
                                                    return@launch
                                                }

                                                if (
                                                    subscriptionResponse.code == 401 ||
                                                    subscriptionResponse.code == 403
                                                ) {
                                                    sessionManager.clearSession()
                                                } else if (!savedName.isNullOrBlank()) {
                                                    navigateToHome()
                                                    return@launch
                                                }
                                            }

                                            if (meResponse.code() == 401 || meResponse.code() == 403) {
                                                sessionManager.clearSession()
                                            } else if (!savedName.isNullOrBlank()) {
                                                navigateToHome()
                                                return@launch
                                            }
                                        } catch (_: Exception) {
                                            if (!savedName.isNullOrBlank()) {
                                                navigateToHome()
                                                return@launch
                                            }
                                        }
                                    } else if (!savedName.isNullOrBlank()) {
                                        sessionManager.clearSession()
                                    }

                                    if (sessionManager.hasAccount()) {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(Screen.Loading.route) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(Screen.Register.route) {
                                            popUpTo(Screen.Loading.route) { inclusive = true }
                                        }
                                    }
                                }
                            }
                        )
                    }

                    composable(Screen.Login.route) { backStackEntry ->
                        // 1. Get the context
                        val context = androidx.compose.ui.platform.LocalContext.current

                        val sessionManager = com.aeu.boxapplication.core.utils.SessionManager.getInstance(context)

                        val loginViewModel = remember(backStackEntry, sessionManager) {
                            ViewModelProvider(
                                backStackEntry,
                                object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        return LoginViewModel(
                                            authService = com.aeu.boxapplication.data.remote.RetrofitClient.authApiService,
                                            sessionManager = sessionManager
                                        ) as T
                                    }
                                }
                            )[LoginViewModel::class.java]
                        }

                        LoginScreen(
                            navController = navController,
                            viewModel = loginViewModel,
                            onLoginSuccess = { _, destination ->
                                when (destination) {
                                    PostLoginDestination.HOME -> {
                                        navController.navigate(Screen.SubscriberHome.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }

                                    PostLoginDestination.SUBSCRIPTIONS_EMPTY -> {
                                        navController.navigate(Screen.SubscriptionsEmpty.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    }
                                }
                            },
                            onBack = { navController.popBackStack() },
                            onSignupClick = { navController.navigate(Screen.Register.route) }
                        )
                    }

                    // --- 2. Register Screen ---
                    composable(Screen.Register.route) { backStackEntry ->
                        val viewModel = remember(backStackEntry) {
                            ViewModelProvider(
                                backStackEntry,
                                RegisterViewModelFactory(AuthRepository(RetrofitClient.authApiService))
                            )[RegisterViewModel::class.java]
                        }
                        RegisterScreen(
                            navController = navController,
                            viewModel = viewModel,
                            onRegisterSuccess = { name, phone, email, token ->
                                sessionManager.saveUserDetail(
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    token = token
                                )
                                sessionManager.setHasAccount()
                                navController.navigate(Screen.SubscriptionsEmpty.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.SubscriptionsEmpty.route) { backStackEntry ->
                        val subscriptionViewModel = remember(backStackEntry, sessionManager) {
                            ViewModelProvider(
                                backStackEntry,
                                object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        return SubscriptionViewModel(
                                            authService = RetrofitClient.authApiService,
                                            sessionManager = sessionManager
                                        ) as T
                                    }
                                }
                            )[SubscriptionViewModel::class.java]
                        }

                        SubscriptionsEmptyScreen(
                            onBack = { navController.popBackStack() },
                            onExplorePlans = { navController.navigate(Screen.ShopProducts.route) },
                            onRestorePurchases = {
                                subscriptionViewModel.restorePurchases(
                                    onHasSubscription = {
                                        navController.navigate(Screen.SubscriberHome.route) {
                                            popUpTo(Screen.SubscriptionsEmpty.route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },
                                    onNoSubscription = {
                                        navController.navigate(Screen.ShopProducts.route)
                                    }
                                )
                            },
                            isLoading = subscriptionViewModel.isLoading
                        )
                    }

                    composable(Screen.ExplorePlans.route) {
                        ShopProductsScreen(
                            viewModel = sharedShopProductsViewModel,
                            onSelectPlan = { selectedPlan ->
                                val defaultOption = defaultFrequencyOption(selectedPlan)
                                sessionManager.savePendingPlan(
                                    planId = defaultOption.id,
                                    planName = selectedPlan.title,
                                    planPrice = defaultOption.price,
                                    planPeriod = defaultOption.period,
                                    planFeatures = selectedPlan.features,
                                    planOptions = selectedPlan.frequencyOptions.map { option ->
                                        PendingPlanOption(
                                            id = option.id,
                                            label = option.label,
                                            price = option.priceLabel,
                                            period = option.periodLabel,
                                            frequencyInDays = option.frequencyInDays
                                        )
                                    }
                                )
                                navController.navigate(Screen.ConfirmSubscription.route)
                            }
                        )
                    }

                    composable(Screen.ConfirmSubscription.route) { backStackEntry ->
                        val subscriptionViewModel = remember(backStackEntry, sessionManager) {
                            ViewModelProvider(
                                backStackEntry,
                                object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        return SubscriptionViewModel(
                                            authService = RetrofitClient.authApiService,
                                            sessionManager = sessionManager
                                        ) as T
                                    }
                                }
                            )[SubscriptionViewModel::class.java]
                        }
                        val pendingPlan = sessionManager.getPendingPlanSelection()
                        val frequencyOptions = pendingPlan?.options.orEmpty()
                        var selectedBillingOptionId by rememberSaveable(pendingPlan?.id) {
                            mutableStateOf(pendingPlan?.id)
                        }
                        val selectedBillingOption = frequencyOptions.firstOrNull { it.id == selectedBillingOptionId }
                            ?: frequencyOptions.firstOrNull()
                        val selectedPlanId = selectedBillingOption?.id ?: pendingPlan?.id
                        val selectedPlanName = pendingPlan?.name ?: "Subscription"
                        val selectedPlanPrice = selectedBillingOption?.price ?: pendingPlan?.price ?: "$19"
                        val selectedPlanPeriod = selectedBillingOption?.period ?: pendingPlan?.period ?: "/mo"

                        ConfirmSubscriptionScreen(
                            onBack = { navController.popBackStack() },
                            onEditPlan = { navController.popBackStack() },
                            onConfirmPay = {
                                if (selectedPlanId.isNullOrBlank()) {
                                    subscriptionViewModel.setError("Please select a plan before confirming.")
                                } else {
                                    sessionManager.savePendingPlan(
                                        planId = selectedPlanId,
                                        planName = selectedPlanName,
                                        planPrice = selectedPlanPrice,
                                        planPeriod = selectedPlanPeriod,
                                        planFeatures = pendingPlan?.features.orEmpty(),
                                        planOptions = frequencyOptions
                                    )
                                    navController.navigate(Screen.PaymentDetails.route)
                                }
                            },
                            selectedPlanName = selectedPlanName,
                            selectedPlanPrice = selectedPlanPrice,
                            selectedPlanPeriod = selectedPlanPeriod,
                            selectedPlanFeatures = resolvePendingPlanFeatures(
                                pendingPlan = pendingPlan,
                                fallbackPlanName = selectedPlanName
                            ),
                            billingOptions = frequencyOptions.map { option ->
                                BillingFrequencyOptionUi(
                                    id = option.id,
                                    label = option.label,
                                    price = option.price,
                                    period = option.period
                                )
                            },
                            selectedBillingOptionId = selectedBillingOption?.id,
                            onSelectBillingOption = { selectedBillingOptionId = it },
                            isSubmitting = subscriptionViewModel.isSubmitting,
                            errorMessage = subscriptionViewModel.errorMessage
                        )
                    }

                    composable(Screen.PaymentDetails.route) { backStackEntry ->
                        val subscriptionViewModel = remember(backStackEntry, sessionManager) {
                            ViewModelProvider(
                                backStackEntry,
                                object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        return SubscriptionViewModel(
                                            authService = RetrofitClient.authApiService,
                                            sessionManager = sessionManager
                                        ) as T
                                    }
                                }
                            )[SubscriptionViewModel::class.java]
                        }
                        val pendingPlan = sessionManager.getPendingPlanSelection()
                        val selectedPlanId = pendingPlan?.id
                        val selectedPlanName = pendingPlan?.name ?: "Subscription"
                        val selectedPlanPrice = pendingPlan?.price ?: "$19"
                        val selectedPlanPeriod = pendingPlan?.period ?: "/mo"
                        var stripePublishableKey by remember { mutableStateOf("") }
                        var pendingPlanId by remember { mutableStateOf<String?>(null) }
                        var pendingStripeSubscriptionId by remember { mutableStateOf<String?>(null) }
                        var pendingStripeConfirmation by remember {
                            mutableStateOf<PendingStripeConfirmation?>(null)
                        }

                        val navigateToPaymentConfirmation: () -> Unit = {
                            navController.navigate(Screen.PaymentConfirmation.route) {
                                popUpTo(Screen.PaymentDetails.route) { inclusive = true }
                            }
                        }

                        val handleSubscriptionActivated: () -> Unit = {
                            sharedSubscriberHomeViewModel.loadDashboard(forceRefresh = true)
                            sharedProfileViewModel.loadProfile(forceRefresh = true)
                            sharedHistoryViewModel.loadHistory(forceRefresh = true)
                            navigateToPaymentConfirmation()
                        }

                        val navigateToSubscriberHome: () -> Unit = {
                            sessionManager.clearPendingPlan()
                            navController.navigate(Screen.SubscriberHome.route) {
                                popUpTo(Screen.ShopProducts.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        val paymentLauncher = rememberPaymentLauncher(
                            publishableKey = stripePublishableKey,
                            stripeAccountId = null,
                            callback = { paymentResult ->
                                when (paymentResult) {
                                    PaymentResult.Completed -> {
                                        val planIdToConfirm = pendingPlanId
                                        val stripeSubscriptionId = pendingStripeSubscriptionId
                                        if (planIdToConfirm.isNullOrBlank() || stripeSubscriptionId.isNullOrBlank()) {
                                            subscriptionViewModel.setError("Missing Stripe subscription data. Please try again.")
                                        } else {
                                            subscriptionViewModel.confirmStripeSubscription(
                                                planId = planIdToConfirm,
                                                stripeSubscriptionId = stripeSubscriptionId,
                                                onSuccess = handleSubscriptionActivated
                                            )
                                        }
                                    }

                                    PaymentResult.Canceled -> {
                                        subscriptionViewModel.setError("Payment canceled.")
                                    }

                                    is PaymentResult.Failed -> {
                                        subscriptionViewModel.setError(
                                            "Payment failed: ${paymentResult.throwable.localizedMessage ?: "Unknown Stripe error"}"
                                        )
                                    }
                                }
                            }
                        )

                        LaunchedEffect(pendingStripeConfirmation, stripePublishableKey) {
                            val confirmation = pendingStripeConfirmation ?: return@LaunchedEffect
                            if (stripePublishableKey.isBlank()) return@LaunchedEffect

                            pendingStripeConfirmation = null
                            try {
                                when (confirmation) {
                                    is PendingStripeConfirmation.PaymentIntent -> {
                                        paymentLauncher.confirm(confirmation.params)
                                    }

                                    is PendingStripeConfirmation.SetupIntent -> {
                                        paymentLauncher.confirm(confirmation.params)
                                    }
                                }
                            } catch (error: Exception) {
                                subscriptionViewModel.setError(
                                    "Payment failed: ${error.localizedMessage ?: "Unknown Stripe error"}"
                                )
                            }
                        }

                        PaymentDetailsScreen(
                            onBack = { navController.popBackStack() },
                            onPayNow = { cardInput ->
                                if (selectedPlanId.isNullOrBlank()) {
                                    subscriptionViewModel.setError("Please select a plan before paying.")
                                } else {
                                    subscriptionViewModel.createStripeCheckout(selectedPlanId) { checkout ->
                                        pendingPlanId = selectedPlanId
                                        pendingStripeSubscriptionId = checkout.stripeSubscriptionId

                                        val canConfirmWithoutSheet = !checkout.requiresPaymentSheet &&
                                            (checkout.stripeSubscriptionStatus == "active" ||
                                                checkout.stripeSubscriptionStatus == "trialing")

                                        when {
                                            !checkout.paymentIntentClientSecret.isNullOrBlank() -> {
                                                stripePublishableKey = checkout.publishableKey
                                                pendingStripeConfirmation = PendingStripeConfirmation.PaymentIntent(
                                                    ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                                                        cardInput.toStripePaymentMethodCreateParams(
                                                            email = sessionManager.getUserEmail()
                                                        ),
                                                        checkout.paymentIntentClientSecret
                                                    )
                                                )
                                            }

                                            !checkout.setupIntentClientSecret.isNullOrBlank() -> {
                                                stripePublishableKey = checkout.publishableKey
                                                pendingStripeConfirmation = PendingStripeConfirmation.SetupIntent(
                                                    ConfirmSetupIntentParams.create(
                                                        cardInput.toStripePaymentMethodCreateParams(
                                                            email = sessionManager.getUserEmail()
                                                        ),
                                                        checkout.setupIntentClientSecret
                                                    )
                                                )
                                            }

                                            canConfirmWithoutSheet -> {
                                                subscriptionViewModel.confirmStripeSubscription(
                                                    planId = selectedPlanId,
                                                    stripeSubscriptionId = checkout.stripeSubscriptionId,
                                                    onSuccess = handleSubscriptionActivated
                                                )
                                            }

                                            !checkout.requiresPaymentSheet -> {
                                                subscriptionViewModel.setError(
                                                    "Stripe checkout is not ready yet (status: ${checkout.stripeSubscriptionStatus ?: "unknown"}). Please try again in a moment."
                                                )
                                            }

                                            else -> {
                                                subscriptionViewModel.setError(
                                                    "Stripe checkout did not return a client secret."
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            selectedPlanName = selectedPlanName,
                            selectedPlanPrice = selectedPlanPrice,
                            selectedPlanPeriod = selectedPlanPeriod,
                            initialCardholderName = sessionManager.getUserName().orEmpty(),
                            isSubmitting = subscriptionViewModel.isSubmitting,
                            errorMessage = subscriptionViewModel.errorMessage,
                            onDismissError = subscriptionViewModel::clearError
                        )
                    }

                    composable(Screen.PaymentConfirmation.route) {
                        val pendingPlan = sessionManager.getPendingPlanSelection()
                        val openDashboard: () -> Unit = {
                            sessionManager.clearPendingPlan()
                            sharedSubscriberHomeViewModel.loadDashboard(forceRefresh = true)
                            sharedProfileViewModel.loadProfile(forceRefresh = true)
                            navController.navigate(Screen.SubscriberHome.route) {
                                popUpTo(Screen.ShopProducts.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        PaymentConfirmationScreen(
                            selectedPlanName = pendingPlan?.name ?: "Subscription",
                            selectedPlanPrice = pendingPlan?.price ?: "$19.00",
                            selectedPlanPeriod = pendingPlan?.period.toReadablePlanPeriod(),
                            shippingAddress = sharedProfileViewModel.uiState.addresses.firstOrNull()?.address,
                            onBack = openDashboard,
                            onViewDashboard = openDashboard,
                            onGoToHistory = {
                                sessionManager.clearPendingPlan()
                                sharedHistoryViewModel.loadHistory(forceRefresh = true)
                                navController.navigate(Screen.OrderHistory.route) {
                                    popUpTo(Screen.ShopProducts.route) { inclusive = true }
                                }
                            }
                        )
                    }


                    composable(
                        route = Screen.SubscriberHome.route
                    ) {
                        val fallbackName = sessionManager.getUserName().takeUnless { it.isNullOrBlank() } ?: "Guest"
                        SubscriberHomeScreen(
                            navController = navController,
                            userName = fallbackName,
                            viewModel = sharedSubscriberHomeViewModel
                        )
                    }
                    // Inside your NavHost
                    composable(Screen.OrderHistory.route) {
                        val historyUserName = sessionManager.getUserName().takeUnless { it.isNullOrBlank() } ?: "User"
                        OrderHistoryScreen(
                            navController = navController,
                            userName = historyUserName,
                            viewModel = sharedHistoryViewModel,
                            onOrderClick = { orderId ->
                                navController.navigate("${Screen.HistoryDetail.route}/$orderId")
                            }
                        )
                    }

                    composable("${Screen.HistoryDetail.route}/{orderId}") { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                        HistoryDetailScreen(
                            orderId = orderId,
                            navController = navController,
                            viewModel = sharedHistoryViewModel
                        )
                    }
                    composable(Screen.ShopProducts.route) {
                        ShopProductsScreen(
                            viewModel = sharedShopProductsViewModel,
                            onSelectPlan = { selectedPlan ->
                                val defaultOption = defaultFrequencyOption(selectedPlan)
                                sessionManager.savePendingPlan(
                                    planId = defaultOption.id,
                                    planName = selectedPlan.title,
                                    planPrice = defaultOption.price,
                                    planPeriod = defaultOption.period,
                                    planFeatures = selectedPlan.features,
                                    planOptions = selectedPlan.frequencyOptions.map { option ->
                                        PendingPlanOption(
                                            id = option.id,
                                            label = option.label,
                                            price = option.priceLabel,
                                            period = option.periodLabel,
                                            frequencyInDays = option.frequencyInDays
                                        )
                                    }
                                )
                                navController.navigate(Screen.ConfirmSubscription.route)
                            }
                        )
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            viewModel = sharedProfileViewModel,
                            activeSubscriptionCount = sharedSubscriberHomeViewModel.uiState.dashboard
                                ?.activeSubscriptions
                                ?.size
                                ?: 0,
                            onShippingAddressClick = { navController.navigate("shipping_address") },
                            onSubscriptionDetailsClick = { navController.navigate(Screen.SubscribDetail.route) },
                            onLogoutClick = {
                                sessionManager.clearSession()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                        )
                    }

                    composable(Screen.ShipAddress.route) {
                        ShippingAddressScreen(
                            navController = navController,
                            viewModel = sharedProfileViewModel
                        )
                    }

                    composable(Screen.AddShipAddress.route) {
                        AddShippingAddressScreen(
                            navController = navController,
                            viewModel = sharedProfileViewModel
                        )
                    }


                    // 4. Secondary/Detail Screens
                    composable(Screen.SubscribDetail.route) {
                        SubscriptionDetailsScreen(
                            navController = navController,
                            homeViewModel = sharedSubscriberHomeViewModel,
                            profileViewModel = sharedProfileViewModel
                        )
                    }
                    composable(Screen.OrderConfirmed.route) { OrderConfirmScreen(navController) }
                    composable(Screen.CheckoutPayment.route) { CheckoutPayment(navController) }
                    composable(Screen.CompletePayment.route) { CompletePayment(navController) }

                        }
                    }

                    AppNotificationHost(
                        hostState = notificationHostState
                    )
                    AppLoadingHost(
                        hostState = loadingHostState
                    )
                }
            }
        }
    }

}

// --- UI Components ---
private fun resolveSubscriberDisplayName(
    name: String?,
    phone: String?,
    email: String?
): String {
    return name?.takeIf { it.isNotBlank() }
        ?: phone?.takeIf { it.isNotBlank() }
        ?: email?.substringBefore("@")?.takeIf { it.isNotBlank() }
        ?: "Subscriber"
}

private fun String?.toReadablePlanPeriod(): String {
    return when (this) {
        "/yr" -> "per year"
        "/3mo" -> "every 3 months"
        "/2wk" -> "every 2 weeks"
        "/wk" -> "per week"
        "/mo" -> "per month"
        null, "" -> "per month"
        else -> this.removePrefix("/")
    }
}

private fun NavHostController.navigateToBottomTab(route: String) {
    if (currentDestination?.route == route) {
        return
    }

    if (route == Screen.SubscriberHome.route) {
        val poppedToHome = popBackStack(Screen.SubscriberHome.route, inclusive = false)
        if (!poppedToHome && currentDestination?.route != Screen.SubscriberHome.route) {
            navigate(Screen.SubscriberHome.route) {
                launchSingleTop = true
                restoreState = true
            }
        }
        return
    }

    navigate(route) {
        popUpTo(Screen.SubscriberHome.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun defaultFrequencyOption(plan: ShopPlanUiModel): PendingPlanOption {
    val selectedOption = plan.frequencyOptions.firstOrNull { it.periodLabel == "/mo" }
        ?: plan.frequencyOptions.firstOrNull()
        ?: return PendingPlanOption(
            id = plan.id,
            label = "Monthly",
            price = plan.priceLabel,
            period = plan.periodLabel,
            frequencyInDays = 30
        )

    return PendingPlanOption(
        id = selectedOption.id,
        label = selectedOption.label,
        price = selectedOption.priceLabel,
        period = selectedOption.periodLabel,
        frequencyInDays = selectedOption.frequencyInDays
    )
}

private fun selectedPlanFeatures(planName: String): List<String> {
    return when (planName.lowercase()) {
        "the wellness box" -> listOf(
            "6 organic self-care essentials",
            "Monthly wellness curation",
            "Free doorstep delivery"
        )

        "eco-home essentials" -> listOf(
            "Zero-waste home supplies",
            "Thoughtfully sourced eco picks",
            "Monthly refill-ready bundle"
        )

        "gamer's loot" -> listOf(
            "Premium gaming accessories",
            "High-performance gear picks",
            "Late-night snack extras"
        )

        "snack stash express" -> listOf(
            "Sweet and savory snack rotation",
            "Seasonal limited-edition treats",
            "Quick monthly delivery"
        )

        "glow ritual box" -> listOf(
            "Beauty and skincare essentials",
            "Glow-focused monthly routine",
            "Exclusive editor picks"
        )

        else -> listOf("Standard subscription access")
    }
}

private fun resolvePendingPlanFeatures(
    pendingPlan: PendingPlanSelection?,
    fallbackPlanName: String
): List<String> {
    return pendingPlan?.features?.takeIf { it.isNotEmpty() }
        ?: selectedPlanFeatures(fallbackPlanName)
}

private sealed class PendingStripeConfirmation {
    data class PaymentIntent(
        val params: ConfirmPaymentIntentParams
    ) : PendingStripeConfirmation()

    data class SetupIntent(
        val params: ConfirmSetupIntentParams
    ) : PendingStripeConfirmation()
}

private fun PaymentCardInput.toStripePaymentMethodCreateParams(
    email: String?
): PaymentMethodCreateParams {
    val card = PaymentMethodCreateParams.Card(
        number = cardNumber,
        expiryMonth = expiryMonth,
        expiryYear = expiryYear,
        cvc = cvv
    )
    val billingDetails = PaymentMethod.BillingDetails(
        address = Address(country = "US"),
        email = email,
        name = cardholderName
    )
    return PaymentMethodCreateParams.create(card, billingDetails)
}

enum class SubscriberBottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    Home("Home", Icons.Default.Home, "subscriber_home"),
    History("History", Icons.Default.Refresh, "order_history"),
    Package("Plans", Icons.Default.Send, "shop_products"),
    Profile("Profile", Icons.Default.Person, "profile_screen")
}

@Composable
fun SubscriberBottomNav(
    modifier: Modifier = Modifier,
    selected: SubscriberBottomNavItem,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onShopClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val activeColor = Color(0xFF1E88E5)
    val inactiveColor = Color(0xFF4B5563)

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 0.dp,
    ) {
        SubscriberBottomNavTab(
            item = SubscriberBottomNavItem.Home,
            selected = selected == SubscriberBottomNavItem.Home,
            onClick = onHomeClick,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )
        SubscriberBottomNavTab(
            item = SubscriberBottomNavItem.History,
            selected = selected == SubscriberBottomNavItem.History,
            onClick = onHistoryClick,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )
        SubscriberBottomNavTab(
            item = SubscriberBottomNavItem.Package,
            selected = selected == SubscriberBottomNavItem.Package,
            onClick = onShopClick,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )
        SubscriberBottomNavTab(
            item = SubscriberBottomNavItem.Profile,
            selected = selected == SubscriberBottomNavItem.Profile,
            onClick = onProfileClick,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )
    }
}

@Composable
private fun RowScope.SubscriberBottomNavTab(
    item: SubscriberBottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    activeColor: Color,
    inactiveColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (selected) activeColor else inactiveColor

    Column(
        modifier = Modifier
            .weight(1f)
            .height(80.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = contentColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
