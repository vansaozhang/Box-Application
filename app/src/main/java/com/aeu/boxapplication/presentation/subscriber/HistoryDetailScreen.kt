package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.domain.model.dummyOrders


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    orderId: String,
    navController: NavController
) {
    // Lookup the order data based on the ID passed from the previous screen
    val order = dummyOrders.find { it.id == orderId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoxlyBackground)
            .statusBarsPadding()
    ) {
        // --- TOP NAVIGATION BAR ---
        CenterAlignedTopAppBar(
            title = { Text("Transaction Details", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        if (order == null) {
            // Error State
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Order #$orderId Not Found", color = Color.Red)
            }
        } else {
            // --- SCROLLABLE CONTENT ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // 1. Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = order.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // 2. Price and Date
                Text(order.subtotal, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                Text(order.date, color = BoxlyTextGrey, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(32.dp))

                // 3. Order Summary Card (Dynamic Items)
                DetailCard(title = "ORDER SUMMARY") {
                    order.items.forEach { item ->
                        SummaryRow(item.name, item.price, isBold = true)
                        Text(item.type, fontSize = 11.sp, color = BoxlyTextGrey)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BoxlyBackground)
                    SummaryRow("Subtotal", order.subtotal)
                    SummaryRow("Shipping", "Free")
                    SummaryRow("Total Charged", order.total, isBold = true, fontSize = 18.sp)
                }

                // 4. Payment Method Card
                DetailCard(title = "PAYMENT METHOD") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF5850EC),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.size(width = 40.dp, height = 24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("VISA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Visa ending in 4242", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("EXP 12/26", color = BoxlyTextGrey, fontSize = 12.sp)
                        }
                    }
                }

                // 5. Shipping Address Card
                DetailCard(title = "SHIPPING ADDRESS") {
                    Row {
                        Surface(
                            color = BoxlyBackground,
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BoxlyTextGrey, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Jane Doe", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("123 Sunshine Boulevard, Apt 4B\nLos Angeles, CA 90001",
                                color = BoxlyTextGrey, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                    }
                }

                // 6. Management Actions
                DetailCard(title = "SUBSCRIPTION MANAGEMENT") {
                    ManagementRow(Icons.Default.Pause, Color(0xFFFEF3C7), Color(0xFFD97706), "Pause Subscription")
                    ManagementRow(Icons.Default.Add, Color(0xFFD1FAE5), Color(0xFF059669), "Upgrade Plan")
                    ManagementRow(Icons.Default.Close, Color(0xFFFEE2E2), Color(0xFFDC2626), "Cancel Subscription")
                }

                // 7. Action Buttons
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Action */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Download Invoice", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(title, fontSize = 12.sp, color = BoxlyTextGrey, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 0.5.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) { content() }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isBold: Boolean = false, fontSize: androidx.compose.ui.unit.TextUnit = 14.sp) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = fontSize, color = if (isBold) Color.Black else BoxlyTextGrey, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium)
        Text(value, fontSize = fontSize, color = Color.Black, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium)
    }
}

@Composable
fun ManagementRow(icon: ImageVector, iconBg: Color, iconTint: Color, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(8.dp), color = iconBg, modifier = Modifier.size(32.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.padding(6.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCBD5E1))
    }
}