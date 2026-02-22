package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.domain.model.OrderItemInfo
import com.aeu.boxapplication.domain.model.dummyOrders

@Composable
fun OrderDetailsScreen(
    orderId: String,
    navController: NavController,
    onBack: () -> Unit = { navController.navigateUp() }
) {
    // 1. DATA LOOKUP: Find the specific order from our dummy database
    val order = dummyOrders.find { it.id == orderId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F9))
    ) {
        if (order == null) {
            // Error handling if ID is invalid
            Text(
                "Order #$orderId Not Found",
                Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // 2. HEADER
                OrderDetailsTopBar(orderId = order.id, onBack = onBack)

                Spacer(modifier = Modifier.height(16.dp))

                // 3. STATUS & PROGRESS
                OrderStatusCard(
                    status = order.status,
                    date = order.date,
                    progress = order.progress
                )

                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel(text = "ITEMS (${order.items.size})")
                Spacer(modifier = Modifier.height(8.dp))

                // 4. DYNAMIC ITEMS LIST
                OrderItemsCard(order.items)

                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel(text = "ORDER INFORMATION")
                Spacer(modifier = Modifier.height(8.dp))

                // 5. STATIC INFO (Shipping/Payment)
                OrderInformationCard()

                Spacer(modifier = Modifier.height(24.dp))

                // 6. TOTALS
                OrderTotalsCard(subtotal = order.subtotal, total = order.total)

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun OrderDetailsTopBar(orderId: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = "Order #$orderId",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        // Placeholder for Help/Info
        IconButton(onClick = { }) {
            Icon(Icons.Outlined.Info, contentDescription = "Help")
        }
    }
}

@Composable
private fun OrderStatusCard(status: String, date: String, progress: Float) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("STATUS", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(status, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                    Text(date, fontSize = 12.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFFE3F2FD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = Color(0xFF1E88E5))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Progress Bar
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFFF1F5F9), CircleShape)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress) // Fills based on 0.0 to 1.0
                        .height(6.dp)
                        .background(Color(0xFF1E88E5), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun OrderItemsCard(items: List<OrderItemInfo>) {
    Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
        Column(modifier = Modifier.padding(16.dp)) {
            items.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(item.color.copy(0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = item.color)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(item.type, fontSize = 11.sp, color = Color.Gray)
                    }
                    Text(item.price, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                if (index < items.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                }
            }
        }
    }
}

@Composable
private fun OrderInformationCard() {
    Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoItem(Icons.Outlined.LocationOn, "Shipping Address", "Jane Doe\n123 App Street, NY 10001")
            Spacer(modifier = Modifier.height(16.dp))
            InfoItem(Icons.Outlined.Payments, "Payment Method", "Visa ending in 4242")
        }
    }
}

@Composable
private fun OrderTotalsCard(subtotal: String, total: String) {
    Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
        Column(modifier = Modifier.padding(20.dp)) {
            TotalRow("Subtotal", subtotal)
            TotalRow("Shipping", "Free")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
            TotalRow("Total", total, isTotal = true)
        }
    }
}

// --- HELPER UI COMPONENTS ---

@Composable
private fun InfoItem(icon: ImageVector, label: String, value: String) {
    Row {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 13.sp, color = Color.Black)
        }
    }
}

@Composable
private fun TotalRow(label: String, value: String, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = if(isTotal) Color.Black else Color.Gray, fontWeight = if(isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontWeight = FontWeight.Bold, fontSize = if(isTotal) 18.sp else 14.sp, color = if(isTotal) Color(0xFF1E88E5) else Color.Black)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
}