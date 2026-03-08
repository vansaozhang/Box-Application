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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
        isVisible = dashboardState.isLoading && subscription == null && profileState.profile == null
    )

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
        },
        bottomBar = {
            Box(modifier = Modifier.padding(20.dp)) {
                Button(
                    onClick = {
                        if (subscription == null) {
                            navController.navigate(Screen.ExplorePlans.route)
                        } else {
                            navController.navigate(Screen.ShipAddress.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DetailsPrimary)
                ) {
                    Text(
                        if (subscription == null) "Explore Plans" else "Manage Shipping Address",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
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
                    onExplore = { navController.navigate(Screen.ExplorePlans.route) }
                )
                return@Column
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
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
        shape = RoundedCornerShape(20.dp),
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
        shape = RoundedCornerShape(24.dp),
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
                    imageVector = Icons.Outlined.Refresh,
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
                Text("Explore Plans", color = Color.White)
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
