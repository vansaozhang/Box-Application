package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
private val HomeTint = Color(0xFFEAF3FF)
private val HomeHeroTop = Color(0xFFFDFEFF)
private val HomeHeroBottom = Color(0xFFE7F0FF)
private val HomeSuccess = Color(0xFF16A34A)
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
    val hasActiveSubscription = activeSubscription != null

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    // Navigate to login if session expired
    LaunchedEffect(uiState.isAuthenticationError) {
        if (uiState.isAuthenticationError) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    AppGlobalLoadingEffect(isVisible = uiState.isLoading && dashboard == null)

    Scaffold(
        containerColor = HomeBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            DashboardHeader(
                userName = userName,
                activeSubscriptionCount = activeSubscriptions.size,
                latestShipment = dashboard?.latestShipment
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
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .padding(bottom = 24.dp)
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

                if (hasActiveSubscription) {
                    DashboardSummaryRow(
                        activeSubscriptionCount = activeSubscriptions.size,
                        shipment = dashboard?.latestShipment
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BillingCard(
                        billing = dashboard?.billing
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TrackingCard(
                        shipment = dashboard?.latestShipment,
                        onTrack = { navController.navigate(Screen.OrderHistory.route) }
                    )
                } else {
                    EmptyDashboardHeroCard(
                        onExplore = { navController.navigate(Screen.ShopProducts.route) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DeliveryPreviewCard()
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (hasActiveSubscription) {
                    SectionTitle("Your Subscription")
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionTitle("Why subscribe")
                        TextButton(
                            onClick = { navController.navigate(Screen.ShopProducts.route) }
                        ) {
                            Text(
                                text = "See plans",
                                color = HomePrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (!hasActiveSubscription) {
                    EmptySubscriptionsCard(
                        onExplore = { navController.navigate(Screen.ShopProducts.route) }
                    )
                } else {
                    SubscriptionListItem(
                        subscription = activeSubscription,
                        onClick = { navController.navigate(Screen.SubscribDetail.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    userName: String,
    activeSubscriptionCount: Int,
    latestShipment: DashboardShipmentResponse?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(HomeHeroTop, HomeHeroBottom)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(HomePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = HomePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DASHBOARD",
                            fontSize = 11.sp,
                            color = HomePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Welcome, $userName",
                            fontSize = 28.sp,
                            color = HomeTitle,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Your billing, shipments, and plan updates in one place.",
                            modifier = Modifier.padding(top = 4.dp),
                            fontSize = 13.sp,
                            color = HomeBody,
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardHeaderChip(
                        text = if (activeSubscriptionCount > 0) {
                            "$activeSubscriptionCount active plan" +
                                if (activeSubscriptionCount > 1) "s" else ""
                        } else {
                            "No active plan"
                        }
                    )
                    DashboardHeaderChip(
                        text = latestShipment?.let(::shipmentStatusTitle) ?: "No shipment yet",
                        accent = if (latestShipment != null) HomeSuccess else HomeBody
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeaderChip(
    text: String,
    accent: Color = HomePrimary
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.14f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DashboardSummaryRow(
    activeSubscriptionCount: Int,
    shipment: DashboardShipmentResponse?
) {
    val shipmentDate = formatShortDate(shipment?.estimatedDeliveryDate ?: shipment?.shipmentDate)
        ?: "Not scheduled"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryMetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.ShoppingCart,
            title = "Active plans",
            value = activeSubscriptionCount.toString(),
            subtitle = if (activeSubscriptionCount == 1) "subscription running" else "subscriptions running",
            accent = HomePrimary
        )
        SummaryMetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalShipping,
            title = "Shipment",
            value = shipment?.let(::shipmentStatusTitle) ?: "Queued",
            subtitle = shipmentDate,
            accent = if (shipment != null) HomeSuccess else HomePending
        )
    }
}

@Composable
private fun SummaryMetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    accent: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = HomeCard,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title.uppercase(Locale.US),
                    fontSize = 10.sp,
                    color = HomeBody,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = value,
                modifier = Modifier.padding(top = 12.dp),
                fontSize = 20.sp,
                color = HomeTitle,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 12.sp,
                color = HomeBody,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun BillingCard(
    billing: DashboardBillingResponse?
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
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = HomeCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionEyebrow(
                    icon = Icons.Default.DateRange,
                    label = "Next billing"
                )

                Spacer(modifier = Modifier.weight(1f))

                StatusBadge(
                    text = if (hasSubscriptions) {
                        "${billing?.subscriptionCount ?: 0} ACTIVE"
                    } else {
                        "READY"
                    },
                    color = if (hasSubscriptions) HomePrimary else HomePending
                )
            }

            Text(
                text = billingDate,
                modifier = Modifier.padding(top = 18.dp),
                fontSize = 30.sp,
                color = HomeTitle,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 34.sp
            )
            Text(
                text = totalCharge,
                fontSize = 14.sp,
                color = HomeBody,
                modifier = Modifier.padding(top = 4.dp),
                lineHeight = 20.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatTile(
                    modifier = Modifier.weight(1f),
                    label = "Total due",
                    value = CurrencyUtils.formatPrice(billing?.totalAmount ?: 0.0)
                )
                DashboardStatTile(
                    modifier = Modifier.weight(1f),
                    label = "Plans billed",
                    value = if (hasSubscriptions) {
                        "${billing?.subscriptionCount ?: 0} active"
                    } else {
                        "Choose one"
                    }
                )
            }

        }
    }
}

@Composable
private fun SectionEyebrow(
    icon: ImageVector,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HomePrimary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label.uppercase(Locale.US),
            fontSize = 11.sp,
            color = HomePrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DashboardStatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = HomeTint.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Text(
                text = label.uppercase(Locale.US),
                fontSize = 10.sp,
                color = HomeBody,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 15.sp,
                color = HomeTitle,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyDashboardHeroCard(onExplore: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = HomeCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6DB7FF), HomePrimary, Color(0xFF1D3F8F))
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 20.dp)
            ) {
                Column {
                    Surface(
                        color = Color.White.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = "GET STARTED",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Build your first box",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Pick a plan, choose a delivery cadence, and manage every order from this dashboard.",
                        modifier = Modifier.padding(top = 6.dp),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.88f),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeroTag("Free shipping")
                HeroTag("Save 15%")
                HeroTag("Skip anytime")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onExplore,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HomePrimary)
            ) {
                Text(
                    text = "Browse Boxes",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun HeroTag(label: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = HomePrimary.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, HomePrimary.copy(alpha = 0.12f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = HomePrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DeliveryPreviewCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = HomeCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "HOW IT WORKS",
                fontSize = 11.sp,
                color = HomePrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your billing and delivery timeline will show up here after your first subscription starts.",
                modifier = Modifier.padding(top = 6.dp),
                fontSize = 15.sp,
                color = HomeTitle,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            DeliveryStepRow(
                icon = Icons.Default.ShoppingCart,
                title = "Choose a box",
                description = "Browse curated plans and select the one that fits your routine."
            )
            Spacer(modifier = Modifier.height(12.dp))
            DeliveryStepRow(
                icon = Icons.Default.DateRange,
                title = "Set your schedule",
                description = "Pick weekly, monthly, or yearly billing in a few taps."
            )
            Spacer(modifier = Modifier.height(12.dp))
            DeliveryStepRow(
                icon = Icons.Default.LocalShipping,
                title = "Track every delivery",
                description = "Recharge dates, shipments, and order history will appear here."
            )
        }
    }
}

@Composable
private fun DeliveryStepRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(HomePrimary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HomePrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HomeTitle
            )
            Text(
                text = description,
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 12.sp,
                color = HomeBody,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun TrackingCard(
    shipment: DashboardShipmentResponse?,
    onTrack: () -> Unit
) {
    val steps = shipment?.steps ?: defaultShipmentSteps()
    val shipmentAccent = shipmentStatusColor(shipment?.status)

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = HomeCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionEyebrow(
                        icon = Icons.Default.LocalShipping,
                        label = "Shipment progress"
                    )
                    Text(
                        text = shipmentStatusTitle(shipment),
                        fontSize = 24.sp,
                        color = HomeTitle,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Text(
                        text = shipmentStatusSubtitle(shipment),
                        fontSize = 14.sp,
                        color = HomeBody,
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 20.sp
                    )
                }

                StatusBadge(
                    text = shipmentBadgeLabel(shipment?.status),
                    color = shipmentAccent
                )
            }

            shipment?.subscriptionName?.takeIf { it.isNotBlank() }?.let { subscriptionName ->
                Text(
                    text = subscriptionName,
                    modifier = Modifier.padding(top = 12.dp),
                    fontSize = 14.sp,
                    color = HomeTitle,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!shipment?.trackingNumber.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.padding(top = 12.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = shipmentAccent.copy(alpha = 0.10f)
                ) {
                    Text(
                        text = "Tracking ${shipment?.trackingNumber}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        color = shipmentAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.forEach { step ->
                    ShipmentStepCard(
                        step = step,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Button(
                onClick = onTrack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HomeTint,
                    contentColor = HomePrimary
                )
            ) {
                Text(
                    text = if (shipment != null) "Track Shipment" else "Open History",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ShipmentStepCard(
    step: DashboardShipmentStepResponse,
    modifier: Modifier = Modifier
) {
    val semanticAccent = shipmentStepColor(step.key)
    val accent = when {
        step.current -> semanticAccent
        step.completed -> semanticAccent
        else -> HomeBody
    }
    val background = when {
        step.current -> semanticAccent.copy(alpha = 0.12f)
        step.completed -> semanticAccent.copy(alpha = 0.06f)
        else -> Color.White
    }
    val borderColor = when {
        step.current -> semanticAccent.copy(alpha = 0.30f)
        step.completed -> semanticAccent.copy(alpha = 0.14f)
        else -> HomeStroke
    }
    val labelColor = when {
        step.current || step.completed -> HomeTitle
        else -> HomeBody
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = background,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Text(
                text = step.label,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 11.sp,
                color = labelColor,
                fontWeight = if (step.current) FontWeight.Bold else FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SubscriptionListItem(
    subscription: DashboardSubscriptionResponse,
    onClick: () -> Unit
) {
    val statusColor = subscriptionStatusColor(subscription.billingStatus)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        color = HomeCard,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(HomePrimary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = HomePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CURRENT PLAN",
                        fontSize = 10.sp,
                        color = HomePrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subscription.name,
                        fontSize = 22.sp,
                        color = HomeTitle,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                StatusBadge(
                    text = subscription.billingStatus.uppercase(Locale.US),
                    color = statusColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatTile(
                    modifier = Modifier.weight(1f),
                    label = "Price",
                    value = formatSubscriptionPrice(subscription)
                )
                DashboardStatTile(
                    modifier = Modifier.weight(1f),
                    label = "Renews",
                    value = formatShortDate(subscription.rechargeDate) ?: "TBD"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View details",
                    color = HomePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = HomePrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.18f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun shipmentStatusColor(status: String?): Color {
    return when (status?.uppercase(Locale.US)) {
        "DELIVERED" -> HomeSuccess
        "SHIPPED" -> HomePrimary
        "PACKED" -> HomePending
        "PENDING" -> HomePending
        else -> HomeBody
    }
}

private fun shipmentBadgeLabel(status: String?): String {
    return when (status?.uppercase(Locale.US)) {
        "DELIVERED" -> "DELIVERED"
        "SHIPPED" -> "IN TRANSIT"
        "PACKED" -> "PACKED"
        else -> "PENDING"
    }
}

private fun shipmentStepColor(key: String): Color {
    return when (key.uppercase(Locale.US)) {
        "PENDING" -> HomePrimary
        "PACKED" -> HomePending
        "SHIPPED" -> Color(0xFF0EA5E9)
        "DELIVERED" -> HomeSuccess
        else -> HomePrimary
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
                text = "Subscriber benefits",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTitle
            )
            Text(
                text = "Recurring plans are built for repeat orders, better pricing, and a delivery schedule you control.",
                fontSize = 13.sp,
                color = HomeBody,
                modifier = Modifier.padding(top = 4.dp),
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            SubscriptionBenefitRow(
                icon = Icons.Default.LocalShipping,
                title = "Free shipping",
                description = "Included on every recurring delivery cycle."
            )
            Spacer(modifier = Modifier.height(12.dp))
            SubscriptionBenefitRow(
                icon = Icons.Default.Star,
                title = "Better pricing",
                description = "Subscribers get the best price compared to one-time orders."
            )
            Spacer(modifier = Modifier.height(12.dp))
            SubscriptionBenefitRow(
                icon = Icons.Default.DateRange,
                title = "Flexible cadence",
                description = "Pause, skip, or change your plan whenever you need."
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onExplore,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("See all plans", color = HomePrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SubscriptionBenefitRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(HomePrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HomePrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HomeTitle
            )
            Text(
                text = description,
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 12.sp,
                color = HomeBody,
                lineHeight = 18.sp
            )
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
        DashboardShipmentStepResponse("PENDING", "Order Placed", completed = false, current = false),
        DashboardShipmentStepResponse("PACKED", "Packed", completed = false, current = false),
        DashboardShipmentStepResponse("SHIPPED", "Shipped", completed = false, current = false),
        DashboardShipmentStepResponse("DELIVERED", "Delivered", completed = false, current = false)
    )
}

private fun shipmentStatusTitle(shipment: DashboardShipmentResponse?): String {
    return when (shipment?.status?.uppercase(Locale.US)) {
        "DELIVERED" -> "Delivered"
        "SHIPPED" -> "In Transit"
        "PACKED" -> "Packed"
        "PENDING" -> "Order Placed"
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
        "PACKED" -> "Packed on ${formatShortDate(shipment.shipmentDate) ?: displayDate}"
        else -> "Order placed on ${formatShortDate(shipment.shipmentDate) ?: displayDate}"
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
