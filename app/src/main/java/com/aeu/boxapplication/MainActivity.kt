package com.aeu.boxapplication
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aeu.boxapplication.core.utils.SessionManager
import com.aeu.boxapplication.data.remote.RetrofitClient
import com.aeu.boxapplication.domain.repository.AuthRepository
import com.aeu.boxapplication.presentation.LoginViewModel
import com.aeu.boxapplication.presentation.auth.LoginScreen
import com.aeu.boxapplication.presentation.auth.RegisterScreen
import com.aeu.boxapplication.presentation.auth.RegisterViewModel
import com.aeu.boxapplication.presentation.auth.RegisterViewModelFactory
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.presentation.onboarding.LoadingScreen
import com.aeu.boxapplication.presentation.profile.ShippingAddressScreen
import com.aeu.boxapplication.presentation.subscription.ConfirmSubscriptionScreen
import com.aeu.boxapplication.presentation.subscription.ExplorePlansScreen
import com.aeu.boxapplication.presentation.subscription.SubscriptionsEmptyScreen
import com.aeu.boxapplication.presentation.subscriber.*

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

            // Define which screens should show the Bottom Navigation Bar
            val showBottomBar = currentRoute?.startsWith(Screen.SubscriberHome.route) == true ||
                    currentRoute == Screen.OrderHistory.route ||
                    currentRoute == Screen.ShopProducts.route ||
                    currentRoute == Screen.Profile.route

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
                        LoadingScreen(
                            onFinished = {
                                val savedName = sessionManager.getUserName()
                                val hasAccount = sessionManager.hasAccount()

                                when {
                                    // Case 1: Currently Logged In
                                    !savedName.isNullOrEmpty() -> {
                                        val encodedName = Uri.encode(savedName)
                                        navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                            popUpTo(Screen.Loading.route) { inclusive = true }
                                        }
                                    }
                                    // Case 2: Has account but logged out
                                    hasAccount -> {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(Screen.Loading.route) { inclusive = true }
                                        }
                                    }
                                    // Case 3: First time user (No account yet)
                                    else -> {
                                        navController.navigate(Screen.Register.route) {
                                            popUpTo(Screen.Loading.route) { inclusive = true }
                                        }
                                    }
                                }
                            }
                        )
                    }

                    composable(Screen.Login.route) {
                        // 1. Get the context
                        val context = androidx.compose.ui.platform.LocalContext.current

                        val sessionManager = com.aeu.boxapplication.core.utils.SessionManager.getInstance(context)

                        val loginViewModel: LoginViewModel = viewModel(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                    // 3. Pass BOTH parameters to the constructor
                                    return LoginViewModel(
                                        authService = com.aeu.boxapplication.data.remote.RetrofitClient.authApiService,
                                        sessionManager = sessionManager
                                    ) as T
                                }
                            }
                        )

                        LoginScreen(
                            navController = navController,
                            viewModel = loginViewModel,
                            onLoginSuccess = { userName ->
                                // This is where the actual navigation happens
                                val encodedName = Uri.encode(userName)
                                navController.navigate("${Screen.SubscriberHome.route}/$encodedName") {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onBack = { navController.popBackStack() },
                            onSignupClick = { navController.navigate(Screen.Register.route) }
                        )
                    }

                    // --- 2. Register Screen ---
                    composable(Screen.Register.route) {
                        val viewModel: RegisterViewModel = viewModel(
                            factory = RegisterViewModelFactory(AuthRepository(RetrofitClient.authApiService))
                        )
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

                    composable(Screen.SubscriptionsEmpty.route) {
                        SubscriptionsEmptyScreen(
                            onBack = { navController.popBackStack() },
                            onExplorePlans = { navController.navigate(Screen.ExplorePlans.route) }
                        )
                    }

                    composable(Screen.ExplorePlans.route) {
                        ExplorePlansScreen(
                            onBack = { navController.popBackStack() },
                            onSelectStarter = { navController.navigate(Screen.ConfirmSubscription.route) },
                            onSelectPro = { navController.navigate(Screen.ConfirmSubscription.route) },
                            onSelectBusiness = { navController.navigate(Screen.ConfirmSubscription.route) }
                        )
                    }

                    composable(Screen.ConfirmSubscription.route) {
                        ConfirmSubscriptionScreen(
                            onBack = { navController.popBackStack() },
                            onEditPlan = { navController.popBackStack() },
                            onConfirmPay = { navController.navigate(Screen.CheckoutPayment.route) }
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
                        OrderHistoryScreen(
                            navController = navController,
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
        }
    }

}

// --- UI Components ---
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
    val bottomNavItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = activeColor,
        selectedTextColor = activeColor,
        unselectedIconColor = inactiveColor,
        unselectedTextColor = inactiveColor,
        indicatorColor = Color.Transparent
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 0.dp,
    ) {
        NavigationBarItem(
            selected = selected == SubscriberBottomNavItem.Home,
            onClick = onHomeClick,
            label = { Text("Home") },
            icon = { Icon(SubscriberBottomNavItem.Home.icon, contentDescription = null) },
            colors = bottomNavItemColors
        )
        NavigationBarItem(
            selected = selected == SubscriberBottomNavItem.History,
            onClick = onHistoryClick,
            label = { Text("History") },
            icon = { Icon(SubscriberBottomNavItem.History.icon, contentDescription = null) },
            colors = bottomNavItemColors
        )
        NavigationBarItem(
            selected = selected == SubscriberBottomNavItem.Package,
            onClick = onShopClick,
            label = { Text("Package") },
            icon = { Icon(SubscriberBottomNavItem.Package.icon, contentDescription = null) },
            colors = bottomNavItemColors
        )
        NavigationBarItem(
            selected = selected == SubscriberBottomNavItem.Profile,
            onClick = onProfileClick,
            label = { Text("Profile") },
            icon = { Icon(SubscriberBottomNavItem.Profile.icon, contentDescription = null) },
            colors = bottomNavItemColors
        )
    }
}
