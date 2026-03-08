package com.aeu.boxapplication
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.aeu.boxapplication.presentation.profile.ShippingAddressScreen
import com.aeu.boxapplication.presentation.subscription.ConfirmSubscriptionScreen
import com.aeu.boxapplication.presentation.subscription.ExplorePlansScreen
import com.aeu.boxapplication.presentation.subscription.SubscriptionsEmptyScreen
import com.aeu.boxapplication.presentation.subscription.SubscriptionViewModel
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
            val sessionManager = remember { SessionManager.getInstance(context) }
            val notificationHostState = rememberAppNotificationHostState()
            val loadingHostState = rememberAppLoadingHostState()

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
                                        val homeUserName = sessionManager.getUserName().takeUnless { it.isNullOrBlank() } ?: "Guest"
                                        val encodedName = Uri.encode(homeUserName)
                                        navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    onHistoryClick = {
                                        navController.navigate(Screen.OrderHistory.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    onShopClick = {
                                        navController.navigate(Screen.ShopProducts.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    onProfileClick = {
                                        navController.navigate(Screen.Profile.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
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
                                    val navigateToHome: (String) -> Unit = { userName ->
                                        val encodedName = Uri.encode(userName)
                                        navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                            popUpTo(Screen.Loading.route) { inclusive = true }
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
                                                    ?: meBody.email.substringBefore("@")

                                                val subscriptionResponse = RetrofitClient.authApiService
                                                    .getMySubscriptionSafely("Bearer $savedToken")
                                                if (
                                                    subscriptionResponse.isSuccessful ||
                                                    subscriptionResponse.code == 404
                                                ) {
                                                    sessionManager.saveUserDetail(
                                                        name = resolvedName,
                                                        email = meBody.email,
                                                        token = savedToken
                                                    )
                                                    sessionManager.setHasAccount()

                                                    val hasActiveSubscription = subscriptionResponse
                                                        .hasActiveSubscription
                                                    if (hasActiveSubscription) {
                                                        navigateToHome(resolvedName)
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
                                                    navigateToHome(savedName)
                                                    return@launch
                                                }
                                            }

                                            if (meResponse.code() == 401 || meResponse.code() == 403) {
                                                sessionManager.clearSession()
                                            } else if (!savedName.isNullOrBlank()) {
                                                navigateToHome(savedName)
                                                return@launch
                                            }
                                        } catch (_: Exception) {
                                            if (!savedName.isNullOrBlank()) {
                                                navigateToHome(savedName)
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
                            onLoginSuccess = { userName, destination ->
                                when (destination) {
                                    PostLoginDestination.HOME -> {
                                        val encodedName = Uri.encode(userName)
                                        navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                            popUpTo(Screen.Login.route) { inclusive = true }
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
                            onRegisterSuccess = { name, email, token ->
                                sessionManager.saveUserDetail(
                                    name = name,
                                    email = email,
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
                            onExplorePlans = { navController.navigate(Screen.ExplorePlans.route) },
                            onRestorePurchases = {
                                subscriptionViewModel.restorePurchases(
                                    onHasSubscription = {
                                        val homeUserName = sessionManager.getUserName().takeUnless { it.isNullOrBlank() } ?: "User"
                                        val encodedName = Uri.encode(homeUserName)
                                        navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                            popUpTo(Screen.SubscriptionsEmpty.route) { inclusive = true }
                                        }
                                    },
                                    onNoSubscription = {
                                        navController.navigate(Screen.ExplorePlans.route)
                                    }
                                )
                            },
                            isLoading = subscriptionViewModel.isLoading
                        )
                    }

                    composable(Screen.ExplorePlans.route) { backStackEntry ->
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

                        LaunchedEffect(Unit) {
                            if (subscriptionViewModel.plans.isEmpty()) {
                                subscriptionViewModel.loadPlans()
                            }
                        }

                        ExplorePlansScreen(
                            isLoading = subscriptionViewModel.isLoading,
                            isMonthly = subscriptionViewModel.selectedCycle.apiValue == "monthly",
                            plans = subscriptionViewModel.plans,
                            errorMessage = subscriptionViewModel.errorMessage,
                            onBack = { navController.popBackStack() },
                            onRestorePurchases = {
                                subscriptionViewModel.restorePurchases(
                                    onHasSubscription = {
                                        val homeUserName = sessionManager.getUserName().takeUnless { it.isNullOrBlank() } ?: "User"
                                        val encodedName = Uri.encode(homeUserName)
                                        navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                            popUpTo(Screen.ExplorePlans.route) { inclusive = true }
                                        }
                                    },
                                    onNoSubscription = {}
                                )
                            },
                            onToggleMonthly = { isMonthly ->
                                subscriptionViewModel.setBillingCycle(isMonthly = isMonthly)
                            },
                            onSelectPlan = { selectedPlan ->
                                sessionManager.savePendingPlan(
                                    planId = selectedPlan.id,
                                    planName = selectedPlan.name,
                                    planPrice = selectedPlan.priceLabel,
                                    planPeriod = selectedPlan.periodLabel
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
                        val selectedPlanId = sessionManager.getPendingPlanId()
                        val selectedPlanName = sessionManager.getPendingPlanName() ?: "Pro"
                        val selectedPlanPrice = sessionManager.getPendingPlanPrice() ?: "$19"
                        val selectedPlanPeriod = sessionManager.getPendingPlanPeriod() ?: "/mo"

                        ConfirmSubscriptionScreen(
                            onBack = { navController.popBackStack() },
                            onEditPlan = { navController.popBackStack() },
                            onConfirmPay = {
                                if (selectedPlanId.isNullOrBlank()) {
                                    subscriptionViewModel.setError("Please select a plan before confirming.")
                                } else {
                                    navController.navigate(Screen.PaymentDetails.route)
                                }
                            },
                            selectedPlanName = selectedPlanName,
                            selectedPlanPrice = selectedPlanPrice,
                            selectedPlanPeriod = selectedPlanPeriod,
                            selectedPlanFeatures = selectedPlanFeatures(selectedPlanName),
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
                        val selectedPlanId = sessionManager.getPendingPlanId()
                        val selectedPlanName = sessionManager.getPendingPlanName() ?: "Pro"
                        val selectedPlanPrice = sessionManager.getPendingPlanPrice() ?: "$19"
                        val selectedPlanPeriod = sessionManager.getPendingPlanPeriod() ?: "/mo"
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

                        val navigateToSubscriberHome: () -> Unit = {
                            sessionManager.clearPendingPlan()
                            val homeUserName = sessionManager.getUserName().takeUnless { it.isNullOrBlank() } ?: "User"
                            val encodedName = Uri.encode(homeUserName)
                            navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                popUpTo(Screen.ExplorePlans.route) { inclusive = true }
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
                                                onSuccess = navigateToPaymentConfirmation
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
                                                    onSuccess = navigateToPaymentConfirmation
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
                            isSubmitting = subscriptionViewModel.isSubmitting,
                            errorMessage = subscriptionViewModel.errorMessage
                        )
                    }

                    composable(Screen.PaymentConfirmation.route) {
                        PaymentConfirmationScreen(
                            onViewDashboard = {
                                sessionManager.clearPendingPlan()
                                val homeUserName = sessionManager.getUserName().takeUnless { it.isNullOrBlank() } ?: "User"
                                val encodedName = Uri.encode(homeUserName)
                                navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                    popUpTo(Screen.ExplorePlans.route) { inclusive = true }
                                }
                            },
                            onGoToHistory = {
                                sessionManager.clearPendingPlan()
                                navController.navigate(Screen.OrderHistory.route) {
                                    popUpTo(Screen.ExplorePlans.route) { inclusive = true }
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
                            userName = fallbackName
                        )
                    }

                    composable(
                        route = "${Screen.SubscriberHome.route}/{userName}",
                        arguments = listOf(navArgument("userName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val extractedName = backStackEntry.arguments?.getString("userName") ?: "Guest"

                        SubscriberHomeScreen(
                            navController = navController,
                            userName = extractedName
                        )
                    }
                    // Inside your NavHost
                    composable(Screen.OrderHistory.route) {
                        val historyUserName = sessionManager.getUserName().takeUnless { it.isNullOrBlank() } ?: "User"
                        OrderHistoryScreen(
                            navController = navController,
                            userName = historyUserName,
                            onOrderClick = { orderId ->
                                navController.navigate("${Screen.HistoryDetail.route}/$orderId")
                            }
                        )
                    }

                    composable("${Screen.HistoryDetail.route}/{orderId}") { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                        HistoryDetailScreen(
                            orderId = orderId,
                            navController = navController
                        )
                    }
                    composable(Screen.ShopProducts.route) {
                        ShopProductsScreen(navController)
                    }
                    composable(Screen.Profile.route) {
                        // 1. Get the data from your session/storage
                        val userName = sessionManager.getUserName() ?: "User"
                        val userEmail = sessionManager.getUserEmail() ?: "No Email"

                        ProfileScreen(
                            navController = navController,
                            userName = userName,      // 2. Pass it here
                            userEmail = userEmail,    // 3. Pass it here
                            onShippingAddressClick = { navController.navigate("shipping_address") },
                            onPaymentMethodsClick = { navController.navigate("payment_methods") },
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
                        ShippingAddressScreen(navController)
                    }


                    // 4. Secondary/Detail Screens
                    composable(Screen.SubscribDetail.route) { SubscriptionDetailsScreen(navController) }
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
private fun selectedPlanFeatures(planName: String): List<String> {
    return when (planName.lowercase()) {
        "starter" -> listOf(
            "5 recurring orders",
            "Basic analytics",
            "Free shipping"
        )

        "pro" -> listOf(
            "Unlimited recurring orders",
            "Advanced analytics",
            "Free shipping on all orders",
            "Priority support"
        )

        "business" -> listOf(
            "Everything in Pro",
            "Multiple user seats",
            "Dedicated account manager"
        )

        else -> listOf("Standard subscription access")
    }
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
    Package("Package", Icons.Default.Send, "shop_products"),
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
