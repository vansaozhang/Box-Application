package com.aeu.boxapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.aeu.boxapplication.presentation.auth.LoginScreen
import com.aeu.boxapplication.presentation.auth.RegisterScreen
import com.aeu.boxapplication.presentation.onboarding.LoadingScreen
import com.aeu.boxapplication.presentation.onboarding.WelcomeScreen
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.presentation.payment.PaymentConfirmationScreen
import com.aeu.boxapplication.presentation.payment.PaymentDetailsScreen
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
                NavHost(
                    navController = navController,
                    startDestination = Screen.Loading.route
                ) {
                    composable(Screen.Loading.route) {
                        LoadingScreen(
                            onFinished = {
                                navController.navigate(Screen.Welcome.route) {
                                    popUpTo(Screen.Loading.route) { inclusive = true }
                                }
                            }
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
                            onGoToHistory = { navController.popBackStack() }
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
