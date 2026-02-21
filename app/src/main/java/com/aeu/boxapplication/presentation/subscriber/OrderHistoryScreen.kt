package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderHistoryScreen(
    onOrderClick: () -> Unit = {},
    onLoadPrevious: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 84.dp)
        ) {
            OrderHistoryHeader(onNotificationsClick = onNotificationsClick)
            Spacer(modifier = Modifier.height(18.dp))
            OrderHistoryTitle()
            Spacer(modifier = Modifier.height(16.dp))
            OrderHistoryList(onOrderClick = onOrderClick)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Load previous orders",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E88E5),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLoadPrevious)
                    .padding(vertical = 6.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OrderHistoryHeader(onNotificationsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Sarah",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "Member since 2021",
                    fontSize = 12.sp,
                    color = Color(0xFF7A8699)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFE6E8EC), CircleShape)
                .clickable(onClick = onNotificationsClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFF111827),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun OrderHistoryTitle() {
    Column {
        Text(
            text = "History",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Track your past boxes and re-orders",
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
private fun OrderHistoryList(onOrderClick: () -> Unit) {
    val orders = listOf(
        OrderHistoryItem(
            title = "September Collection",
            date = "Sep 15, 2023",
            type = "Subscription Box",
            price = "$45.00",
            status = "Delivered",
            statusBg = Color(0xFFE6F7EE),
            statusColor = Color(0xFF1E9E62),
            imageLabel = "SEP",
            imageColors = listOf(Color(0xFFFFB26B), Color(0xFFE3741B))
        ),
        OrderHistoryItem(
            title = "Ethiopian Roast",
            date = "Sep 02, 2023",
            type = "Re-order",
            price = "$18.00",
            status = "Delivered",
            statusBg = Color(0xFFE6F7EE),
            statusColor = Color(0xFF1E9E62),
            imageLabel = "ROAST",
            imageColors = listOf(Color(0xFF1F1B16), Color(0xFF4B3D2E))
        ),
        OrderHistoryItem(
            title = "August Collection",
            date = "Aug 15, 2023",
            type = "Subscription Box",
            price = "$45.00",
            status = "Delivered",
            statusBg = Color(0xFFE6F7EE),
            statusColor = Color(0xFF1E9E62),
            imageLabel = "AUG",
            imageColors = listOf(Color(0xFF2F2F2F), Color(0xFF5A5A5A))
        ),
        OrderHistoryItem(
            title = "Dark Chocolate Pack",
            date = "Aug 05, 2023",
            type = "Re-order",
            price = "$12.50",
            status = "Delivered",
            statusBg = Color(0xFFE6F7EE),
            statusColor = Color(0xFF1E9E62),
            imageLabel = "DARK",
            imageColors = listOf(Color(0xFF3A1F1A), Color(0xFF6B2A1C))
        ),
        OrderHistoryItem(
            title = "July Collection",
            date = "Jul 15, 2023",
            type = "Subscription Box",
            price = "$45.00",
            status = "Archived",
            statusBg = Color(0xFFF2F4F7),
            statusColor = Color(0xFF6B7280),
            imageLabel = "JUL",
            imageColors = listOf(Color(0xFFBDBDBD), Color(0xFFE0E0E0))
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        orders.forEach { order ->
            OrderHistoryCard(order = order, onClick = onOrderClick)
        }
    }
}

@Composable
private fun OrderHistoryCard(
    order: OrderHistoryItem,
    onClick: () -> Unit
) {
    Surface(
        // Use the built-in onClick for better Ripple handling
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Added small vertical spacing so cards don't touch
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Left Image Box ---
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(order.imageColors)),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = order.imageLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // --- Center Text Info ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "${order.date} • ${order.type}",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = order.price,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(order.statusBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = order.status,
                            fontSize = 10.sp,
                            color = order.statusColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // --- Right Arrow Icon ---
            Box(
                modifier = Modifier
                    .size(32.dp) // Slightly increased for better visual balance
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = "View Details",
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private data class OrderHistoryItem(
    val title: String,
    val date: String,
    val type: String,
    val price: String,
    val status: String,
    val statusBg: Color,
    val statusColor: Color,
    val imageLabel: String,
    val imageColors: List<Color>
)
