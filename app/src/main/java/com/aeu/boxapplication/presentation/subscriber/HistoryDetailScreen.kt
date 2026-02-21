package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen (navController: NavController) {
    Scaffold(
        containerColor = BoxlyBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Transaction Details", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BoxlyBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. Header: Status, Amount, and Date ---
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = BoxlyTeal.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    "PAID",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = BoxlyTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            Text("$42.12", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            Text("October 12, 2023", color = BoxlyTextGrey, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. Order Summary Card ---
            DetailCard(title = "ORDER SUMMARY") {
                SummaryRow("The Artisan Collection Box", "$34.99", isBold = true)
                Spacer(modifier = Modifier.height(16.dp))
                SummaryRow("Subtotal", "$34.99")
                SummaryRow("Shipping", "$4.99")
                SummaryRow("Estimated Tax", "$2.14")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BoxlyBackground)
                SummaryRow("Total Charged", "$42.12", isBold = true, fontSize = 18.sp)
            }

            // --- 3. Payment Method Card ---
            DetailCard(title = "PAYMENT METHOD") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF5850EC), // Visa Blue
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

            // --- 4. Shipping Address Card ---
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
                        Text("123 Sunshine Boulevard, Apt 4B\nLos Angeles, CA 90001\nUnited States",
                            color = BoxlyTextGrey, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
            }

            // --- 5. Subscription Management Card ---
            DetailCard(title = "SUBSCRIPTION MANAGEMENT") {
                ManagementRow(icon = Icons.Default.Clear, iconBg = Color(0xFFFEF3C7), iconTint = Color(0xFFD97706), label = "Pause Subscription")
                ManagementRow(icon = Icons.Default.Add, iconBg = Color(0xFFD1FAE5), iconTint = Color(0xFF059669), label = "Upgrade Plan")
                ManagementRow(icon = Icons.Default.Close, iconBg = Color(0xFFFEE2E2), iconTint = Color(0xFFDC2626), label = "Cancel Subscription")
            }

            // --- 6. Footer Buttons ---
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Action */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Download Invoice", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { /* Action */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text("Contact Support", color = Color(0xFF475569), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- Helper Components ---

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
        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(8.dp), color = iconBg, modifier = Modifier.size(32.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.padding(6.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFCBD5E1))
    }
}