package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppStatusBanner
import com.aeu.boxapplication.ui.components.AppStatusTone

private val DetailsPrimary = Color(0xFF1E88E5)
private val DetailsTitle = Color(0xFF2F3A4A)
private val DetailsBody = Color(0xFF7B8794)
private val DetailsStroke = Color(0xFFE3E8EF)
private val DetailsTint = Color(0xFFEAF3FF)
private val DetailsBackground = Color(0xFFF8FAFB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    navController: NavController,
    homeViewModel: SubscriberHomeViewModel,
    profileViewModel: SubscriberProfileViewModel
) {
    val dashboardState = homeViewModel.uiState
    val profileState = profileViewModel.uiState
    val subscription = dashboardState.dashboard?.activeSubscriptions?.firstOrNull()
    val latestShipment = dashboardState.dashboard?.latestShipment
    val primaryAddress = profileState.addresses.firstOrNull()

    LaunchedEffect(Unit) {
        homeViewModel.loadDashboard()
        profileViewModel.loadProfile()
    }

    AppGlobalLoadingEffect(
        isVisible = dashboardState.isManagingSubscription ||
            (dashboardState.isLoading && subscription == null && profileState.profile == null)
    )

    var pendingAction by rememberSaveable { mutableStateOf<SubscriptionAction?>(null) }

    pendingAction?.let { action ->
        AlertDialog(
            onDismissRequest = {
                if (!dashboardState.isManagingSubscription) {
                    pendingAction = null
                }
            },
            title = {
                Text(
                    text = when (action) {
                        SubscriptionAction.Pause -> "Pause subscription?"
                        SubscriptionAction.Resume -> "Resume subscription?"
                        SubscriptionAction.Cancel -> "Cancel subscription?"
                    }
                )
            },
            text = {
                Text(
                    text = when (action) {
                        SubscriptionAction.Pause ->
                            "This will stop future deliveries until you resume the subscription."
                        SubscriptionAction.Resume ->
                            "This will restart billing and deliveries for your current plan."
                        SubscriptionAction.Cancel ->
                            "This cancels the current subscription immediately. You can subscribe again later."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (action) {
                            SubscriptionAction.Pause -> homeViewModel.pauseCurrentSubscription()
                            SubscriptionAction.Resume -> homeViewModel.resumeCurrentSubscription()
                            SubscriptionAction.Cancel -> homeViewModel.cancelCurrentSubscription()
                        }
                        pendingAction = null
                    },
                    enabled = !dashboardState.isManagingSubscription
                ) {
                    Text(
                        text = when (action) {
                            SubscriptionAction.Pause -> "Pause"
                            SubscriptionAction.Resume -> "Resume"
                            SubscriptionAction.Cancel -> "Cancel subscription"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingAction = null },
                    enabled = !dashboardState.isManagingSubscription
                ) {
                    Text("Keep current")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Subscription Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DetailsTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DetailsTitle
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        state = rememberScrollState(),
                        flingBehavior = ScrollableDefaults.flingBehavior()
                    )
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
            dashboardState.errorMessage?.let { message ->
                AppStatusBanner(
                    title = "Subscription overview unavailable",
                    message = message,
                    tone = AppStatusTone.Error,
                    onDismiss = homeViewModel::dismissError
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (subscription == null) {
                EmptySubscriptionState(
                    onExplore = { navController.navigate(Screen.ShopProducts.route) }
                )
                return@Column
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, DetailsStroke)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(color = DetailsTint, shape = RoundedCornerShape(10.dp)) {
                        Text(
                            text = subscription.status,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DetailsPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(subscription.name, color = DetailsTitle, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = com.aeu.boxapplication.core.utils.CurrencyUtils.formatPrice(subscription.price),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = DetailsPrimary
                        )
                        Text(
                            text = frequencyLabel(subscription.frequencyInDays),
                            color = DetailsBody,
                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Billing status: ${subscription.billingStatus}",
                        color = DetailsBody,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Next recharge: ${subscription.rechargeDate ?: "Unavailable"}",
                        color = DetailsBody,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            DetailSectionCard(
                title = "Manage Subscription",
                icon = Icons.Outlined.Settings
            ) {
                Text(
                    text = when (subscription.status.uppercase()) {
                        "PAUSED" -> "Your plan is paused. Resume when you're ready to start deliveries again."
                        else -> "Make changes to your current plan or update where your deliveries go."
                    },
                    color = DetailsBody,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        pendingAction = if (subscription.status.uppercase() == "PAUSED") {
                            SubscriptionAction.Resume
                        } else {
                            SubscriptionAction.Pause
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !dashboardState.isManagingSubscription,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DetailsPrimary)
                ) {
                    Text(
                        text = if (dashboardState.isManagingSubscription) {
                            "Updating..."
                        } else if (subscription.status.uppercase() == "PAUSED") {
                            "Resume Subscription"
                        } else {
                            "Pause Subscription"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { navController.navigate(Screen.ShipAddress.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !dashboardState.isManagingSubscription,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DetailsStroke),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DetailsTitle)
                ) {
                    Text(
                        text = "Manage Shipping Address",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { pendingAction = SubscriptionAction.Cancel },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !dashboardState.isManagingSubscription,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF3C2C2)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB42318))
                ) {
                    Text(
                        text = "Cancel Subscription",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            DetailSectionCard(
                title = "Shipping Address",
                icon = Icons.Outlined.LocationOn
            ) {
                Text(
                    text = primaryAddress?.address ?: "No shipping address on file yet.",
                    color = DetailsBody,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                primaryAddress?.phone?.takeIf { it.isNotBlank() }?.let { phone ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(phone, color = DetailsBody, fontSize = 13.sp)
                }
            }

            DetailSectionCard(
                title = "Latest Shipment",
                icon = Icons.Outlined.LocalShipping
            ) {
                if (latestShipment == null) {
                    Text("No shipment has been created yet.", color = DetailsBody, fontSize = 13.sp)
                } else {
                    DetailRow("Status", latestShipment.status)
                    DetailRow("Shipped", latestShipment.shipmentDate)
                    DetailRow("ETA", latestShipment.estimatedDeliveryDate ?: "Unavailable")
                    DetailRow("Tracking", latestShipment.trackingNumber ?: "Unavailable")
                }
            }
        }
    }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        color = DetailsBackground,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DetailsStroke)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(DetailsTint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Icon(icon, contentDescription = null, tint = DetailsPrimary)
                }
                Spacer(modifier = Modifier.size(12.dp))
                Text(title, fontWeight = FontWeight.Bold, color = DetailsTitle, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        })
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = DetailsBody, fontSize = 13.sp)
        Text(value, color = DetailsTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EmptySubscriptionState(onExplore: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, DetailsStroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(DetailsTint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = DetailsPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("No active subscription", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DetailsTitle)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Start with a plan from the package catalog and your subscription details will appear here.",
                fontSize = 13.sp,
                color = DetailsBody
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onExplore,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DetailsPrimary)
            ) {
                Text("Browse Boxes", color = Color.White)
            }
        }
    }
}

private fun frequencyLabel(days: Int): String {
    return when {
        days >= 360 -> "/yr"
        days == 14 -> "/2wk"
        days == 7 -> "/wk"
        else -> "/mo"
    }
}

private enum class SubscriptionAction {
    Pause,
    Resume,
    Cancel
}
