package com.aeu.boxapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aeu.boxapplication.presentation.auth.RegisterScreen
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.presentation.onboarding.LoadingScreen
import com.aeu.boxapplication.presentation.subscriber.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Define which screens should show the Bottom Navigation Bar
            // We exclude Register and Loading from this list
            val showBottomBar = currentRoute in listOf(
                Screen.SubscriberHome.route,
                Screen.OrderHistory.route,
                Screen.ShopProducts.route,
                Screen.Profile.route
            )

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        SubscriberBottomNav(
                            selected = when(currentRoute) {
                                Screen.OrderHistory.route -> SubscriberBottomNavItem.History
                                Screen.ShopProducts.route -> SubscriberBottomNavItem.Package
                                Screen.Profile.route -> SubscriberBottomNavItem.Profile
                                else -> SubscriberBottomNavItem.Home
                            },
                            onHomeClick = {
                                navController.navigate(Screen.SubscriberHome.route) {
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
                                // After loading, go to Register
                                navController.navigate(Screen.Register.route) {
                                    popUpTo(Screen.Loading.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Register Screen
                    composable(Screen.Register.route) {
                        RegisterScreen(
                            navController = navController,
                            onRegisterSuccess = {
                                // After success, go to Home and clear Register from history
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Main Bottom Nav Screens
                    composable(Screen.SubscriberHome.route) {
                        SubscriberHomeScreen(navController)
                    }
                    composable(Screen.OrderHistory.route) {
                        OrderHistoryScreen { /* Handle detail click if needed */ }
                    }
                    composable(Screen.ShopProducts.route) {
                        ShopProductsScreen(navController)
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(navController)
                    }

                    // 4. Secondary/Detail Screens
                    composable(Screen.SubscribDetail.route) { SubscriptionDetailsScreen(navController) }
                    composable(Screen.OrderConfirmed.route) { OrderConfirmScreen(navController) }
                    composable(Screen.CheckoutPayment.route) { CheckoutPayment(navController) }
                    composable(Screen.CompletePayment.route) { CompletePayment(navController) }
                    composable(Screen.HistoryDetail.route) { HistoryDetailScreen(navController) }
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
    Package("Package", Icons.Default.Favorite, "shop_products"),
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
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        NavigationBarItem(
            selected = selected == SubscriberBottomNavItem.Home,
            onClick = onHomeClick,
            label = { Text("Home") },
            icon = { Icon(SubscriberBottomNavItem.Home.icon, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selected == SubscriberBottomNavItem.History,
            onClick = onHistoryClick,
            label = { Text("History") },
            icon = { Icon(SubscriberBottomNavItem.History.icon, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selected == SubscriberBottomNavItem.Package,
            onClick = onShopClick,
            label = { Text("Package") },
            icon = { Icon(SubscriberBottomNavItem.Package.icon, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selected == SubscriberBottomNavItem.Profile,
            onClick = onProfileClick,
            label = { Text("Profile") },
            icon = { Icon(SubscriberBottomNavItem.Profile.icon, contentDescription = null) }
        )
    }
}