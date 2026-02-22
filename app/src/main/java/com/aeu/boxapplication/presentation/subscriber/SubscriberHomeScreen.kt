package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- Color Palette ---

val BoxlyCardBackground = Color(0xFFFFFFFF)
val BoxlyTextGrey = Color(0xFF94A3B8)
val BoxlyOrange = Color(0xFFFDBA74)

@Composable
fun SubscriberHomeScreen(navController: NavController,userName: String) {
    // 1. UI State for Dialogs
    var showPauseDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BoxlyBackground,
        topBar = { DashboardHeader(userName = userName) },    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // 2. Billing Section
            BillingCard()

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Tracking Section
            TrackingCard()

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Active Subscriptions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle("Active Subscriptions")
                Text("View All", color = BoxlyTeal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            SubscriptionListItem("Gourmet Coffee Box", "Oct 24, 2023", "$19.99/mo", "PAID", BoxlyTeal)
            SubscriptionListItem("Artisan Tea Selection", "Oct 28, 2023", "$10.00/mo", "PENDING", BoxlyOrange)
            SubscriptionListItem("Premium Sound Stream", "Nov 02, 2023", "$14.99/mo", "PAID", BoxlyTeal)

            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- Dialog Logic ---
        if (showPauseDialog) {
            ActionConfirmDialog(
                title = "Pause Subscription?",
                desc = "Your deliveries will stop until you resume. You won't be charged during this time.",
                confirmText = "Pause Plan",
                onDismiss = { showPauseDialog = false },
                onConfirm = { showPauseDialog = false }
            )
        }

        if (showCancelDialog) {
            ActionConfirmDialog(
                title = "Cancel Subscription?",
                desc = "Are you sure? You will lose your current loyalty discount and any pending rewards.",
                confirmText = "Yes, Cancel",
                isDestructive = true,
                onDismiss = { showCancelDialog = false },
                onConfirm = { showCancelDialog = false }
            )
        }
    }
}

// --- Components ---

@Composable
fun DashboardHeader(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(BoxlyTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = BoxlyTeal)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("DASHBOARD", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                // This must match the name above exactly (case sensitive!)
                Text("Welcome, $userName", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)            }
        }
        IconButton(onClick = {}, modifier = Modifier.background(Color.White, CircleShape)) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF1E293B))
        }
    }
}

@Composable
fun ManagePlanCard(onUpgradeClick: () -> Unit, onPauseClick: () -> Unit, onCancelClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = BoxlyCardBackground
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Upgrade to Pro", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Faster delivery & 24/7 support.", color = BoxlyTextGrey, fontSize = 14.sp)
                }
                Button(
                    onClick = onUpgradeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Upgrade", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BoxlyBackground)

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onPauseClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pause", color = Color(0xFF1E293B))
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = BoxlyTextGrey)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel", color = Color(0xFF1E293B))
                }
            }
        }
    }
}

@Composable
fun ActionConfirmDialog(title: String, desc: String, confirmText: String, isDestructive: Boolean = false, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(desc, color = BoxlyTextGrey) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = if (isDestructive) Color(0xFFEF4444) else BoxlyTeal)
            ) { Text(confirmText, color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Back", color = BoxlyTextGrey) }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@Composable
fun BillingCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = BoxlyCardBackground
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = BoxlyTeal, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("NEXT BILLING DATE", fontSize = 12.sp, color = BoxlyTeal, fontWeight = FontWeight.ExtraBold)
            }
            Text("October 24, 2023", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 4.dp))
            Text("Total to be charged: $29.99", color = BoxlyTextGrey, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal),
                shape = RoundedCornerShape(12.dp)
            ) { Text("View Details", color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun TrackingCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = BoxlyCardBackground
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("TRACK YOUR ORDER", fontSize = 11.sp, color = BoxlyTeal, fontWeight = FontWeight.ExtraBold)
                Text("In Transit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Arriving Oct 22, 2023", color = BoxlyTextGrey, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = 0.6f,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = BoxlyTeal, trackColor = BoxlyBackground
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal), shape = RoundedCornerShape(10.dp)) {
                Text("Track", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SubscriptionListItem(name: String, date: String, price: String, status: String, statusColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = BoxlyCardBackground
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(54.dp).clip(RoundedCornerShape(12.dp)).background(BoxlyBackground), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = BoxlyTextGrey)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Recharge Date: $date", fontSize = 12.sp, color = BoxlyTextGrey)
                Text(price, fontSize = 14.sp, color = BoxlyTeal, fontWeight = FontWeight.Bold)
            }
            Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
}

