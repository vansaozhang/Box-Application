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
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Route
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
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppStatusBanner
import com.aeu.boxapplication.ui.components.AppStatusTone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    orderId: String,
    navController: NavController,
    viewModel: SubscriberHistoryViewModel
) {
    val uiState = viewModel.uiState
    val detail = uiState.selectedDetail?.takeIf { it.id == orderId }

    LaunchedEffect(orderId) {
        viewModel.loadDetail(orderId)
    }

    AppGlobalLoadingEffect(isVisible = uiState.isDetailLoading && detail == null)

    Scaffold(
        containerColor = BoxlyBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Shipment Details", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BoxlyBackground)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        state = rememberScrollState(),
                        flingBehavior = ScrollableDefaults.flingBehavior()
                    )
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 16.dp)
            ) {
            when {
                uiState.errorMessage != null && detail == null -> {
                    AppStatusBanner(
                        title = "Shipment detail unavailable",
                        message = uiState.errorMessage,
                        tone = AppStatusTone.Error,
                        onDismiss = viewModel::dismissError
                    )
                }

                detail == null -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE3E8EF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Shipment detail is unavailable right now.", color = BoxlyTextGrey)
                        }
                    }
                }

                else -> {
                    StatusHeroCard(detail = detail)
                    Spacer(modifier = Modifier.height(16.dp))

                    DetailInfoCard(title = "SHIPMENT") {
                        DetailLine("Plan", detail.title)
                        DetailLine("Shipped", detail.shipmentDateLabel)
                        DetailLine("Status", detail.statusLabel)
                        DetailLine("Tracking", detail.trackingLabel ?: "Not available yet")
                    }

                    DetailInfoCard(title = "PAYMENT") {
                        DetailLine("Amount", detail.amountLabel)
                        DetailLine("Payment Status", detail.paymentStatusLabel)
                        DetailLine("Paid On", detail.paymentDateLabel ?: "Pending")
                    }

                    DetailInfoCard(title = "SUBSCRIPTION") {
                        DetailLine("Subscription Status", detail.subscriptionStatusLabel)
                        DetailLine("Billing Cycle", detail.subscriptionPeriodLabel)
                        DetailLine("Cycle Window", detail.renewalWindowLabel ?: "Unavailable")
                    }

                    DetailInfoCard(title = "CURRENT ADDRESS ON FILE") {
                        DetailLine("Recipient", detail.contactName)
                        DetailLine("Address", detail.addressLabel)
                        DetailLine("Phone", detail.phoneLabel ?: "No phone number on file")
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun StatusHeroCard(detail: ShipmentHistoryDetailUiModel) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE3E8EF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(Color(0xFFEAF3FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = detail.amountLabel,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = detail.shipmentDateLabel,
                color = BoxlyTextGrey,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeroChip(icon = Icons.Outlined.Route, label = detail.statusLabel)
                HeroChip(icon = Icons.Outlined.CreditCard, label = detail.paymentStatusLabel)
            }
        }
    }
}

@Composable
private fun HeroChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1E88E5), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text(label, fontSize = 12.sp, color = Color(0xFF2F3A4A), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DetailInfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = BoxlyTextGrey,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFFE3E8EF))
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = BoxlyTextGrey, fontSize = 13.sp)
        Text(
            text = value,
            color = Color(0xFF1E293B),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
