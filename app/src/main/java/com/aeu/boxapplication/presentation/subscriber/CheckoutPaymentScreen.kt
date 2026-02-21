package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aeu.boxapplication.presentation.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPayment(navController: NavController) {
    var selectedPayment by remember { mutableStateOf("Visa") }
    var isSameAsShipping by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Review & Pay", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(20.dp)) {
                Button(
                    onClick = {
                        navController.navigate(Screen.CompletePayment.route)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal)
                ) {
                    Text("Pay & Subscribe", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            SectionHeader("ORDER SUMMARY")
            OrderSummaryItem()

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("PAYMENT METHOD")
            PaymentMethodCard(
                title = "Visa ending in 4242",
                expiry = "Expires 03/25",
                isSelected = selectedPayment == "Visa",
                isDefault = true,
                onSelect = { selectedPayment = "Visa" }
            )
            PaymentMethodCard(
                title = "Mastercard ending in 8801",
                expiry = "Expires 02/25",
                isSelected = selectedPayment == "Mastercard",
                isDefault = false,
                onSelect = { selectedPayment = "Mastercard" }
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("BILLING ADDRESS")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Same as shipping address", color = Color(0xFF1E293B), fontSize = 15.sp)
                Switch(
                    checked = isSameAsShipping,
                    onCheckedChange = { isSameAsShipping = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BoxlyTeal)
                )
            }

            Text(
                text = "$39.00 / month",
                color = BoxlyTeal,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Divider(color = BoxlyLightGray, thickness = 1.dp)

            // Final Price Breakdown
            PriceRow("Subtotal", "$39.00")
            PriceRow("Shipping", "FREE", isTeal = true)
            PriceRow("Estimated Tax", "$3.12")

            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Due Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("RECURRING CHARGE STARTS NEXT MONTH", fontSize = 10.sp, color = BoxlySecondaryText)
                }
                Text("$42.12", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            LegalInfoBox()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun OrderSummaryItem() {
    Surface(
        color = BoxlyLightGray,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF6B8E6B)),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", fontSize = 30.sp) // Placeholder for image
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text("The Gourmet Snack Box", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Monthly Subscription Plan", color = BoxlySecondaryText, fontSize = 13.sp)
                Text("$39.00 / month", color = BoxlyTeal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun PaymentMethodCard(title: String, expiry: String, isSelected: Boolean, isDefault: Boolean, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onSelect() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, if (isSelected) BoxlyTeal else BoxlyLightGray),
        color = Color.White
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isSelected, onClick = onSelect, colors = RadioButtonDefaults.colors(selectedColor = Color.Black))
            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(expiry, fontSize = 12.sp, color = BoxlySecondaryText)
            }
            if (isDefault) {
                Surface(color = BoxlyLightGray, shape = RoundedCornerShape(4.dp)) {
                    Text("DEFAULT", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BoxlySecondaryText)
                }
            }
        }
    }
}

@Composable
fun PriceRow(label: String, value: String, isTeal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = BoxlySecondaryText, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isTeal) BoxlyTeal else Color.Black)
    }
}

@Composable
fun LegalInfoBox() {
    Surface(
        color = BoxlyTeal.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Icon(Icons.Default.Info, contentDescription = null, tint = BoxlyTeal, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "By clicking Pay & Subscribe, you agree to a monthly charge of $42.12 including taxes until you cancel. Your next billing date will be October 24, 2023.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF334155)
            )
        }
    }
}