package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppStatusBanner
import com.aeu.boxapplication.ui.components.AppStatusTone

private val HistoryPrimary = Color(0xFF1E88E5)
private val HistoryTitle = Color(0xFF2F3A4A)
private val HistoryBody = Color(0xFF7B8794)
private val HistoryStroke = Color(0xFFE3E8EF)
private val HistoryBackground = Color(0xFFF7F7F9)

@Composable
fun OrderHistoryScreen(
    navController: NavController,
    userName: String,
    viewModel: SubscriberHistoryViewModel,
    onOrderClick: (String) -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val uiState = viewModel.uiState

    AppGlobalLoadingEffect(isVisible = uiState.isLoading && uiState.history.isEmpty())

    Scaffold(
        containerColor = HistoryBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(HistoryBackground)
                .padding(paddingValues)
        ) {
            OrderHistoryHeader(
                userName = userName,
                onRefresh = { viewModel.loadHistory(forceRefresh = true) },
                onNotificationsClick = onNotificationsClick
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OrderHistoryTitle()
                }

                uiState.errorMessage?.let { message ->
                    item {
                        AppStatusBanner(
                            title = "Shipment history unavailable",
                            message = message,
                            tone = AppStatusTone.Error,
                            onDismiss = viewModel::dismissError
                        )
                    }
                }

                if (uiState.history.isEmpty() && !uiState.isLoading && uiState.errorMessage == null) {
                    item {
                        EmptyHistoryCard(onRefresh = { viewModel.loadHistory(forceRefresh = true) })
                    }
                } else {
                    items(uiState.history, key = { it.id }) { order ->
                        OrderHistoryCard(
                            order = order,
                            onClick = { onOrderClick(order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryHeader(
    userName: String,
    onRefresh: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint = HistoryPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HistoryTitle)
                Text("Your recent deliveries", fontSize = 12.sp, color = HistoryBody)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Refresh history", tint = HistoryTitle)
            }
            IconButton(onClick = onNotificationsClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = HistoryTitle)
            }
        }
    }
}

@Composable
private fun OrderHistoryTitle() {
    Column {
        Text("Shipment History", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = HistoryTitle)
        Text(
            "Review your recurring deliveries and shipment progress.",
            fontSize = 13.sp,
            color = HistoryBody
        )
    }
}

@Composable
private fun OrderHistoryCard(
    order: ShipmentHistoryUiModel,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HistoryStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = HistoryPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f)
            ) {
                Text(
                    text = order.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = HistoryTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = order.dateLabel,
                    fontSize = 12.sp,
                    color = HistoryBody
                )
                order.trackingLabel?.let { tracking ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tracking,
                        fontSize = 11.sp,
                        color = HistoryPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.amountLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = HistoryTitle
                    )
                    StatusChip(label = order.statusLabel, status = order.status)
                }
            }

            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = null,
                tint = HistoryPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun StatusChip(label: String, status: String) {
    val normalized = status.lowercase()
    val containerColor = when {
        "delivered" in normalized -> Color(0xFFE6F7EE)
        "shipped" in normalized -> Color(0xFFEAF3FF)
        else -> Color(0xFFFFF4E5)
    }
    val contentColor = when {
        "delivered" in normalized -> Color(0xFF1E9E62)
        "shipped" in normalized -> HistoryPrimary
        else -> Color(0xFFB45309)
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun EmptyHistoryCard(onRefresh: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HistoryStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFEAF3FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = HistoryPrimary
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "No shipments yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HistoryTitle
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Once your subscription starts shipping, it will appear here.",
                fontSize = 13.sp,
                color = HistoryBody
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Refresh",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = HistoryPrimary,
                modifier = Modifier.clickable(onClick = onRefresh)
            )
        }
    }
}
