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
import androidx.compose.ui.unit.dp
import com.aeu.boxapplication.presentation.navigation.Screen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aeu.boxapplication.presentation.onboarding.LoadingScreen
import com.aeu.boxapplication.presentation.subscriber.CheckoutPayment
import com.aeu.boxapplication.presentation.subscriber.CompletePayment
import com.aeu.boxapplication.presentation.subscriber.HistoryDetailScreen
import com.aeu.boxapplication.presentation.subscriber.OrderConfirmScreen
//import com.aeu.boxapplication.presentation.profile.ProfileScreen
import com.aeu.boxapplication.presentation.subscriber.OrderHistoryScreen
import com.aeu.boxapplication.presentation.subscriber.ProfileScreen
import com.aeu.boxapplication.presentation.subscriber.ShopProductsScreen
import com.aeu.boxapplication.presentation.subscriber.SubscriberHomeScreen
import com.aeu.boxapplication.presentation.subscriber.SubscriptionDetailsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

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
                            // Logic to highlight the correct icon based on currentRoute
                            selected = when(currentRoute) {
                                Screen.OrderHistory.route -> SubscriberBottomNavItem.History
                                Screen.ShopProducts.route -> SubscriberBottomNavItem.Package
                                Screen.Profile.route -> SubscriberBottomNavItem.Profile
                                else -> SubscriberBottomNavItem.Home
                            },
                            onHomeClick = { navController.navigate(Screen.SubscriberHome.route) { launchSingleTop = true } },
                            onHistoryClick = { navController.navigate(Screen.OrderHistory.route) { launchSingleTop = true } },
                            onShopClick = { navController.navigate(Screen.ShopProducts.route) { launchSingleTop = true } },
                            onProfileClick = {
                                navController.navigate(Screen.Profile.route)
                            }
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Loading.route,
                    modifier = Modifier.padding(innerPadding) // THIS IS THE KEY FIX
                ) {
                    composable(Screen.Loading.route) {
                        LoadingScreen(
                            onFinished = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.Loading.route) { inclusive = true }
                                }                            }
                        )
                    }
                    composable(Screen.SubscriberHome.route) {
                        SubscriberHomeScreen(
                         navController
                        )
                    }
                    composable(Screen.OrderHistory.route){
                        OrderHistoryScreen {  }
                    }
                    composable(Screen.ShopProducts.route){
                        ShopProductsScreen(navController)
                    }
                    composable(Screen.Profile.route){
                        ProfileScreen(navController)
                    }
                    composable(Screen.SubscribDetail.route){
                        SubscriptionDetailsScreen(navController)
                    }
                    composable(Screen.OrderConfirmed.route){
                        OrderConfirmScreen(navController)
                    }
                    composable(Screen.CheckoutPayment.route){
                        CheckoutPayment(navController)
                    }
                    composable(Screen.CompletePayment.route){
                        CompletePayment(navController)
                    }
                    composable(Screen.HistoryDetail.route){
                        HistoryDetailScreen(navController)
                    }
                }
            }
        }
    }
}
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
        containerColor = MaterialTheme.colorScheme.background,
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