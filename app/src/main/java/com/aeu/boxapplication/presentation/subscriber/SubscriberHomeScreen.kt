package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

private val HomeBackground = Color(0xFFF3F5F9)
private val HomeCard = Color(0xFFFFFFFF)
private val HomePrimary = Color(0xFF2F6CE5)
private val HomeTitle = Color(0xFF111827)
private val HomeBody = Color(0xFF64748B)
private val HomeStroke = Color(0xFFE6EAF0)
private val HomePending = Color(0xFFF59E0B)

// Compatibility token used by other screens in the same package.
val BoxlyTextGrey = HomeBody

@Composable
fun SubscriberHomeScreen(navController: NavController, userName: String) {
    Scaffold(
        containerColor = HomeBackground,
        topBar = { DashboardHeader(userName = userName) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            BillingCard()

            Spacer(modifier = Modifier.height(12.dp))

            TrackingCard()

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle("Active Subscriptions")
                TextButton(onClick = {}) {
                    Text("View All", color = HomePrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            SubscriptionListItem(
                name = "Gourmet Coffee Box",
                date = "Oct 24, 2023",
                price = "$19.99/mo",
                status = "PAID",
                statusColor = HomePrimary
            )
            SubscriptionListItem(
                name = "Artisan Tea Selection",
                date = "Oct 28, 2023",
                price = "$10.00/mo",
                status = "PENDING",
                statusColor = HomePending
            )
            SubscriptionListItem(
                name = "Premium Sound Stream",
                date = "Nov 02, 2023",
                price = "$14.99/mo",
                status = "PAID",
                statusColor = HomePrimary
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DashboardHeader(userName: String) {
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
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, HomeStroke, CircleShape)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = HomeTitle)
        }
    }
}

@Composable
private fun BillingCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp),
        shape = RoundedCornerShape(20.dp),
        color = HomeCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, HomeStroke)
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
                    text = "October 24, 2023",
                    fontSize = 21.sp,
                    color = HomeTitle,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total to be charged: $29.99",
                    fontSize = 13.sp,
                    color = HomeBody,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HomePrimary)
            ) {
                Text("View Details", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun TrackingCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(20.dp),
        color = HomeCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, HomeStroke)
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
                        text = "TRACK YOUR ORDER",
                        fontSize = 11.sp,
                        color = HomePrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "In Transit",
                        fontSize = 18.sp,
                        color = HomeTitle,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = "Arriving Oct 22, 2023",
                        fontSize = 13.sp,
                        color = HomeBody
                    )
                }

                Button(
                    onClick = {},
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
                    Text("Packed", fontSize = 11.sp, color = HomeBody)
                    Text("Shipped", fontSize = 11.sp, color = HomePrimary, fontWeight = FontWeight.Bold)
                    Text("Delivered", fontSize = 11.sp, color = HomeBody)
                }

                ShipmentProgressBar(
                    packedProgress = 0.46f,
                    totalProgress = 0.62f,
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
    packedProgress: Float,
    totalProgress: Float,
    modifier: Modifier = Modifier
) {
    val packed = packedProgress.coerceIn(0f, 1f)
    val total = totalProgress.coerceIn(packed, 1f)
    val shipped = (total - packed).coerceAtLeast(0f)
    val packedWeight = if (total > 0f) packed / total else 0f
    val shippedWeight = if (total > 0f) shipped / total else 0f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(HomePrimary.copy(alpha = 0.14f))
    ) {
        if (total > 0f) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(total)
                    .fillMaxHeight()
            ) {
                if (packedWeight > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(packedWeight)
                            .fillMaxHeight()
                            .background(
                                color = HomePrimary,
                                shape = if (shippedWeight > 0f) {
                                    RoundedCornerShape(
                                        topStart = 50.dp,
                                        bottomStart = 50.dp,
                                        topEnd = 0.dp,
                                        bottomEnd = 0.dp
                                    )
                                } else {
                                    RoundedCornerShape(50.dp)
                                }
                            )
                    )
                }
                if (shippedWeight > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(shippedWeight)
                            .fillMaxHeight()
                            .background(
                                color = HomePrimary.copy(alpha = 0.82f),
                                shape = if (packedWeight > 0f) {
                                    RoundedCornerShape(
                                        topStart = 0.dp,
                                        bottomStart = 0.dp,
                                        topEnd = 50.dp,
                                        bottomEnd = 50.dp
                                    )
                                } else {
                                    RoundedCornerShape(50.dp)
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionListItem(
    name: String,
    date: String,
    price: String,
    status: String,
    statusColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp)
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        color = HomeCard,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, HomeStroke)
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
                    text = name,
                    fontSize = 14.sp,
                    color = HomeTitle,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Recharge Date: $date",
                    fontSize = 11.sp,
                    color = HomeBody,
                    modifier = Modifier.padding(top = 1.dp)
                )
                Text(
                    text = price,
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
                    text = status,
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
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 17.sp,
        color = HomeTitle,
        fontWeight = FontWeight.Bold
    )
}
