package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.core.utils.CurrencyUtils
import com.aeu.boxapplication.data.remote.dto.response.DashboardBillingResponse
import com.aeu.boxapplication.data.remote.dto.response.DashboardShipmentResponse
import com.aeu.boxapplication.data.remote.dto.response.DashboardShipmentStepResponse
import com.aeu.boxapplication.data.remote.dto.response.DashboardSubscriptionResponse
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppStatusBanner
import com.aeu.boxapplication.ui.components.AppStatusTone
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val HomeBackground = Color(0xFFF3F5F9)
private val HomeCard = Color(0xFFFFFFFF)
private val HomePrimary = Color(0xFF2F6CE5)
private val HomeTitle = Color(0xFF111827)
private val HomeBody = Color(0xFF64748B)
private val HomeStroke = Color(0xFFE6EAF0)
private val HomePending = Color(0xFFF59E0B)
private val HomeError = Color(0xFFDC2626)

// Compatibility token used by other screens in the same package.
val BoxlyTextGrey = HomeBody

@Composable
fun SubscriberHomeScreen(
    navController: NavController,
    userName: String,
    viewModel: SubscriberHomeViewModel
) {
    val uiState = viewModel.uiState
    val dashboard = uiState.dashboard
    val activeSubscriptions = dashboard?.activeSubscriptions.orEmpty()
    val activeSubscription = activeSubscriptions.firstOrNull()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    AppGlobalLoadingEffect(isVisible = uiState.isLoading && dashboard == null)

    Scaffold(
        containerColor = HomeBackground,
        topBar = {
            DashboardHeader(
                userName = userName,
                onRefresh = { viewModel.loadDashboard(forceRefresh = true) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (uiState.isLoading && dashboard != null) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    color = HomePrimary,
                    trackColor = HomePrimary.copy(alpha = 0.12f)
                )
            }

            uiState.errorMessage?.let { message ->
                AppStatusBanner(
                    title = "Dashboard unavailable",
                    message = message,
                    tone = AppStatusTone.Error,
                    onDismiss = viewModel::dismissError,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            BillingCard(
                billing = dashboard?.billing,
                onViewDetails = {
                    if (activeSubscription != null) {
                        navController.navigate(Screen.SubscribDetail.route)
                    } else {
                        navController.navigate(Screen.ShopProducts.route)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TrackingCard(
                shipment = dashboard?.latestShipment,
                onTrack = { navController.navigate(Screen.OrderHistory.route) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle("Your Subscription")
                TextButton(
                    onClick = {
                        if (activeSubscription != null) {
                            navController.navigate(Screen.SubscribDetail.route)
                        } else {
                            navController.navigate(Screen.ShopProducts.route)
                        }
                    }
                ) {
                    Text(
                        text = if (activeSubscription != null) "Manage" else "Explore",
                        color = HomePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (activeSubscription == null) {
                EmptySubscriptionsCard(
                    onExplore = { navController.navigate(Screen.ShopProducts.route) }
                )
            } else {
                SubscriptionListItem(subscription = activeSubscription)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DashboardHeader(
    userName: String,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(HomePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = HomePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "DASHBOARD",
                    fontSize = 10.sp,
                    color = HomePrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Welcome, $userName",
                    fontSize = 17.sp,
                    color = HomeTitle,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(
            onClick = onRefresh,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, HomeStroke, CircleShape)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Refresh dashboard",
                tint = HomeTitle
            )
        }
    }
}

@Composable
private fun BillingCard(
    billing: DashboardBillingResponse?,
    onViewDetails: () -> Unit
) {
    val hasSubscriptions = (billing?.subscriptionCount ?: 0) > 0
    val billingDate = formatLongDate(billing?.nextBillingDate) ?: "No upcoming billing yet"
    val totalCharge = if (hasSubscriptions) {
        "Total to be charged: ${CurrencyUtils.formatPrice(billing?.totalAmount ?: 0.0)}"
    } else {
        "Choose a plan to start your next delivery."
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp),
        shape = RoundedCornerShape(20.dp),
        color = HomeCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = HomePrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NEXT BILLING DATE",
                    fontSize = 11.sp,
                    color = HomePrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = billingDate,
                    fontSize = 21.sp,
                    color = HomeTitle,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = totalCharge,
                    fontSize = 13.sp,
                    color = HomeBody,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Button(
                onClick = onViewDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HomePrimary)
            ) {
                Text(
                    text = if (hasSubscriptions) "View Details" else "Browse Boxes",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TrackingCard(
    shipment: DashboardShipmentResponse?,
    onTrack: () -> Unit
) {
    val steps = shipment?.steps ?: defaultShipmentSteps()
    val progress = shipment?.progress?.toFloat() ?: 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(174.dp),
        shape = RoundedCornerShape(20.dp),
        color = HomeCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TRACK YOUR SHIPMENT",
                        fontSize = 11.sp,
                        color = HomePrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = shipmentStatusTitle(shipment),
                        fontSize = 18.sp,
                        color = HomeTitle,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = shipmentStatusSubtitle(shipment),
                        fontSize = 13.sp,
                        color = HomeBody
                    )
                }

                Button(
                    onClick = onTrack,
                    enabled = shipment != null,
                    modifier = Modifier.height(42.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HomePrimary)
                ) {
                    Text("Track", fontSize = 13.sp, color = Color.White)
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    steps.forEach { step ->
                        Text(
                            text = step.label,
                            fontSize = 11.sp,
                            color = if (step.current) HomePrimary else HomeBody,
                            fontWeight = if (step.current) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                ShipmentProgressBar(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ShipmentProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(HomePrimary.copy(alpha = 0.14f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
                .background(HomePrimary, RoundedCornerShape(50.dp))
        )
    }
}

@Composable
private fun SubscriptionListItem(subscription: DashboardSubscriptionResponse) {
    val statusColor = subscriptionStatusColor(subscription.billingStatus)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp)
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        color = HomeCard,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HomePrimary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = HomePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subscription.name,
                    fontSize = 14.sp,
                    color = HomeTitle,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Recharge Date: ${formatShortDate(subscription.rechargeDate) ?: "TBD"}",
                    fontSize = 11.sp,
                    color = HomeBody,
                    modifier = Modifier.padding(top = 1.dp)
                )
                Text(
                    text = formatSubscriptionPrice(subscription),
                    fontSize = 13.sp,
                    color = HomePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = subscription.billingStatus.uppercase(Locale.US),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 10.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptySubscriptionsCard(onExplore: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = HomeCard,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "No active subscriptions",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = HomeTitle
            )
            Text(
                text = "Browse the available boxes and choose how often you want them delivered.",
                fontSize = 12.sp,
                color = HomeBody,
                modifier = Modifier.padding(top = 4.dp)
            )
            TextButton(
                onClick = onExplore,
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                Text("Browse boxes", color = HomePrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 17.sp,
        color = HomeTitle,
        fontWeight = FontWeight.Bold
    )
}

private fun defaultShipmentSteps(): List<DashboardShipmentStepResponse> {
    return listOf(
        DashboardShipmentStepResponse("PACKED", "Packed", completed = false, current = true),
        DashboardShipmentStepResponse("SHIPPED", "Shipped", completed = false, current = false),
        DashboardShipmentStepResponse("DELIVERED", "Delivered", completed = false, current = false)
    )
}

private fun shipmentStatusTitle(shipment: DashboardShipmentResponse?): String {
    return when (shipment?.status?.uppercase(Locale.US)) {
        "DELIVERED" -> "Delivered"
        "SHIPPED" -> "In Transit"
        "PENDING" -> "Packed"
        else -> "No active shipment"
    }
}

private fun shipmentStatusSubtitle(shipment: DashboardShipmentResponse?): String {
    if (shipment == null) {
        return "Your upcoming shipment will appear here once it is created."
    }

    val displayDate = formatShortDate(
        shipment.estimatedDeliveryDate ?: shipment.shipmentDate
    ) ?: "TBD"

    return when (shipment.status.uppercase(Locale.US)) {
        "DELIVERED" -> "Delivered on $displayDate"
        "SHIPPED" -> "Arriving $displayDate"
        else -> "Packed on ${formatShortDate(shipment.shipmentDate) ?: displayDate}"
    }
}

private fun subscriptionStatusColor(status: String): Color {
    return when (status.uppercase(Locale.US)) {
        "PAID" -> HomePrimary
        "FAILED" -> HomeError
        else -> HomePending
    }
}

private fun formatSubscriptionPrice(subscription: DashboardSubscriptionResponse): String {
    return buildString {
        append(CurrencyUtils.formatPrice(subscription.price))
        append(frequencySuffix(subscription.frequencyInDays))
    }
}

private fun frequencySuffix(frequencyInDays: Int): String {
    return when {
        frequencyInDays in 28..31 -> "/mo"
        frequencyInDays == 7 -> "/wk"
        frequencyInDays == 14 -> "/2wk"
        frequencyInDays in 89..92 -> "/3mo"
        frequencyInDays in 364..366 -> "/yr"
        else -> ""
    }
}

private fun formatLongDate(value: String?): String? = formatDashboardDate(value, "MMMM dd, yyyy")

private fun formatShortDate(value: String?): String? = formatDashboardDate(value, "MMM dd, yyyy")

private fun formatDashboardDate(value: String?, outputPattern: String): String? {
    if (value.isNullOrBlank()) {
        return null
    }

    val normalizedValue = value.take(10)
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val formatter = SimpleDateFormat(outputPattern, Locale.US)
    val parsedDate = runCatching { parser.parse(normalizedValue) }.getOrNull() ?: return value
    return formatter.format(parsedDate)
}
