package com.aeu.boxapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import com.aeu.boxapplication.presentation.auth.LoginScreen
import com.aeu.boxapplication.presentation.auth.RegisterScreen
import com.aeu.boxapplication.presentation.onboarding.LoadingScreen
import com.aeu.boxapplication.presentation.onboarding.WelcomeScreen
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.presentation.payment.PaymentConfirmationScreen
import com.aeu.boxapplication.presentation.payment.PaymentDetailsScreen
import com.aeu.boxapplication.presentation.subscriber.OrderConfirmedScreen
import com.aeu.boxapplication.presentation.subscriber.OrderDetailsScreen
import com.aeu.boxapplication.presentation.subscriber.OrderHistoryScreen
import com.aeu.boxapplication.presentation.subscriber.OrderPlacedScreen
import com.aeu.boxapplication.presentation.subscriber.ProductDetailsScreen
import com.aeu.boxapplication.presentation.subscriber.CheckoutPaymentScreen
import com.aeu.boxapplication.presentation.subscriber.CheckoutShippingScreen
import com.aeu.boxapplication.presentation.subscriber.ReOrderScreen
import com.aeu.boxapplication.presentation.subscriber.ShopProductsScreen
import com.aeu.boxapplication.presentation.subscriber.SubscriberHomeScreen
import com.aeu.boxapplication.presentation.subscriber.ShoppingCartScreen
import com.aeu.boxapplication.presentation.subscriber.CartItem
import com.aeu.boxapplication.presentation.subscriber.ShopProduct
import com.aeu.boxapplication.presentation.subscriber.demoShopProducts
import com.aeu.boxapplication.presentation.subscription.ConfirmSubscriptionScreen
import com.aeu.boxapplication.presentation.subscription.ExplorePlansScreen
import com.aeu.boxapplication.presentation.subscription.SubscriptionsEmptyScreen
import com.aeu.boxapplication.ui.theme.BoxApplicationTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoxApplicationTheme {
                val navController = rememberNavController()
                val shopProducts = remember { demoShopProducts() }
                val cartItems = remember { mutableStateListOf<CartItem>() }

                val addToCart: (ShopProduct) -> Unit = { product ->
                    val index = cartItems.indexOfFirst { it.product.id == product.id }
                    if (index >= 0) {
                        val item = cartItems[index]
                        cartItems[index] = item.copy(quantity = item.quantity + 1)
                    } else {
                        cartItems.add(CartItem(product, 1))
                    }
                }
                val removeFromCart: (ShopProduct) -> Unit = { product ->
                    val index = cartItems.indexOfFirst { it.product.id == product.id }
                    if (index >= 0) {
                        cartItems.removeAt(index)
                    }
                }
                val decrementCart: (ShopProduct) -> Unit = { product ->
                    val index = cartItems.indexOfFirst { it.product.id == product.id }
                    if (index >= 0) {
                        val item = cartItems[index]
                        if (item.quantity > 1) {
                            cartItems[index] = item.copy(quantity = item.quantity - 1)
                        } else {
                            cartItems.removeAt(index)
                        }
                    }
                }
                val clearCart: () -> Unit = {
                    cartItems.clear()
                }
                NavHost(
                    navController = navController,
                    startDestination = Screen.Loading.route
                ) {
                    composable(Screen.Loading.route) {
                        LoadingScreen(
                            onFinished = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.Loading.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.SubscriberHome.route) {
                        SubscriberHomeScreen(
                            onProductClick = { navController.navigate(Screen.ProductDetails.route) },
                            onHistoryClick = {
                                navController.navigate(Screen.OrderHistory.route) {
                                    launchSingleTop = true
                                }
                            },
                            onShopClick = {
                                navController.navigate(Screen.ShopProducts.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(Screen.OrderHistory.route) {
                        OrderHistoryScreen(
                            onOrderClick = { navController.navigate(Screen.OrderDetails.route) },
                            onHomeClick = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.SubscriberHome.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onShopClick = {
                                navController.navigate(Screen.ShopProducts.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(Screen.ShopProducts.route) {
                        ShopProductsScreen(
                            products = shopProducts,
                            cartItems = cartItems,
                            onProductClick = { navController.navigate(Screen.ProductDetails.route) },
                            onAddToCart = addToCart,
                            onHomeClick = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.SubscriberHome.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onHistoryClick = {
                                navController.navigate(Screen.OrderHistory.route) {
                                    launchSingleTop = true
                                }
                            },
                            onCartClick = { navController.navigate(Screen.ShoppingCart.route) }
                        )
                    }
                    composable(Screen.ShoppingCart.route) {
                        ShoppingCartScreen(
                            cartItems = cartItems,
                            onIncrement = addToCart,
                            onDecrement = decrementCart,
                            onRemove = removeFromCart,
                            onClear = clearCart,
                            onCheckout = { navController.navigate(Screen.Checkout.route) },
                            onHomeClick = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.SubscriberHome.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onHistoryClick = {
                                navController.navigate(Screen.OrderHistory.route) {
                                    launchSingleTop = true
                                }
                            },
                            onShopClick = {
                                navController.navigate(Screen.ShopProducts.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(Screen.Checkout.route) {
                        CheckoutShippingScreen(
                            onBack = { navController.popBackStack() },
                            onContinue = { navController.navigate(Screen.CheckoutPayment.route) },
                            onHomeClick = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.SubscriberHome.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onHistoryClick = {
                                navController.navigate(Screen.OrderHistory.route) {
                                    launchSingleTop = true
                                }
                            },
                            onShopClick = {
                                navController.navigate(Screen.ShopProducts.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(Screen.CheckoutPayment.route) {
                        CheckoutPaymentScreen(
                            onBack = { navController.popBackStack() },
                            onReviewOrder = { navController.navigate(Screen.OrderPlaced.route) },
                            onHomeClick = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.SubscriberHome.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onHistoryClick = {
                                navController.navigate(Screen.OrderHistory.route) {
                                    launchSingleTop = true
                                }
                            },
                            onShopClick = {
                                navController.navigate(Screen.ShopProducts.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(Screen.OrderPlaced.route) {
                        OrderPlacedScreen(
                            onClose = { navController.popBackStack() },
                            onReturnDashboard = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.SubscriberHome.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onViewDetails = { navController.navigate(Screen.OrderDetails.route) }
                        )
                    }
                    composable(Screen.ProductDetails.route) {
                        ProductDetailsScreen(
                            onBack = { navController.popBackStack() },
                            onReOrderClick = { navController.navigate(Screen.ReOrder.route) }
                        )
                    }
                    composable(Screen.ReOrder.route) {
                        ReOrderScreen(
                            onBack = { navController.popBackStack() },
                            onConfirmOrder = { navController.navigate(Screen.OrderConfirmed.route) }
                        )
                    }
                    composable(Screen.OrderConfirmed.route) {
                        OrderConfirmedScreen(
                            onReturnDashboard = {
                                navController.navigate(Screen.SubscriberHome.route) {
                                    popUpTo(Screen.SubscriberHome.route) { inclusive = false }
                                }
                            },
                            onViewOrderDetails = { navController.navigate(Screen.OrderDetails.route) }
                        )
                    }
                    composable(Screen.OrderDetails.route) {
                        OrderDetailsScreen(
                            onBack = { navController.popBackStack() },
                            onHelp = { },
                            onTrackPackage = { }
                        )
                    }
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onStartExploring = { navController.navigate(Screen.Register.route) },
                            onLoginClick = { navController.navigate(Screen.Login.route) }
                        )
                    }
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onBack = { navController.popBackStack() },
                            onSignupClick = { navController.navigate(Screen.Register.route) },
                            onLogin = { navController.navigate(Screen.SubscriptionsEmpty.route) }
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
                            onSelectBusiness = { navController.navigate(Screen.ConfirmSubscription.route) }
                        )
                    }
                    composable(Screen.ConfirmSubscription.route) {
                        ConfirmSubscriptionScreen(
                            onBack = { navController.popBackStack() },
                            onConfirmPay = { navController.navigate(Screen.PaymentDetails.route) }
                        )
                    }
                    composable(Screen.PaymentDetails.route) {
                        PaymentDetailsScreen(
                            onBack = { navController.popBackStack() },
                            onPayNow = { navController.navigate(Screen.PaymentConfirmation.route) }
                        )
                    }
                    composable(Screen.PaymentConfirmation.route) {
                        PaymentConfirmationScreen(
                            onViewDashboard = { navController.popBackStack() },
                            onGoToHistory = { navController.navigate(Screen.OrderHistory.route) }
                        )
                    }
                    composable(Screen.Register.route) {
                        RegisterScreen(
                            onBack = { navController.popBackStack() },
                            onContinue = { navController.navigate(Screen.SubscriptionsEmpty.route) },
                            onLoginClick = { navController.navigate(Screen.Login.route) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    BoxApplicationTheme {
        WelcomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    BoxApplicationTheme {
        RegisterScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    BoxApplicationTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionsEmptyPreview() {
    BoxApplicationTheme {
        SubscriptionsEmptyScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ExplorePlansPreview() {
    BoxApplicationTheme {
        ExplorePlansScreen()
    }
}
