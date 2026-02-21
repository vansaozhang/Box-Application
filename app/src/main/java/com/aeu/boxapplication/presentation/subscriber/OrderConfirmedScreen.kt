package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aeu.boxapplication.presentation.navigation.Screen

// Color Palette based on images
val BoxlySoftTeal = Color(0xFFE0FBFA)
val BoxlyLightGray = Color(0xFFF8FAFB)
val BoxlySecondaryText = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmScreen(navController: NavController) {
    var selectedPlan by remember { mutableStateOf("Yearly") }
    var deliveryFreq by remember { mutableStateOf("Once a Month") }

    Scaffold(
        containerColor = BoxlyLightGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Complete Your Order", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screen.CheckoutPayment.route)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal)
                ) {
                    Text("Subscribe & Pay", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "By clicking Subscribe & Pay, you agree to our Terms of Service. You can cancel your subscription at any time.",
                    fontSize = 11.sp,
                    color = BoxlySecondaryText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            SectionHeader("ORDER SUMMARY")
            OrderSummaryCard()

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("SELECT YOUR PLAN")

            PlanOption(
                title = "Yearly Plan",
                price = "$19.99/mo",
                subtext = "($239.88 billed annually)",
                badge = "BEST VALUE",
                isSelected = selectedPlan == "Yearly",
                onClick = { selectedPlan = "Yearly" }
            )
            PlanOption(
                title = "6-Month Plan",
                price = "$24.99/mo",
                subtext = "($149.94 billed every 6 mo)",
                badge = "SAVE 15%",
                isSelected = selectedPlan == "6-Month",
                onClick = { selectedPlan = "6-Month" }
            )
            PlanOption(
                title = "Monthly",
                price = "$29.99/mo",
                subtext = null,
                badge = null,
                isSelected = selectedPlan == "Monthly",
                onClick = { selectedPlan = "Monthly" }
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("DELIVERY FREQUENCY")
            DeliveryFrequencyToggle(
                selected = deliveryFreq,
                onSelect = { deliveryFreq = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            PriceBreakdown(selectedPlan)
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun OrderSummaryCard() {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1589710780350-1215889378f1?w=400", // Representative box image
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text("The Wellness Box", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Curated monthly essentials for your mental and physical wellbeing.",
                    fontSize = 13.sp, color = BoxlySecondaryText, lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun PlanOption(
    title: String, price: String, subtext: String?,
    badge: String?, isSelected: Boolean, onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(2.dp, if (isSelected) BoxlyTeal else Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = BoxlyTeal)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = BoxlyTeal, shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp, fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
                Row {
                    Text(text = price, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF475569))
                    if (subtext != null) {
                        Text(text = " $subtext", fontSize = 14.sp, color = BoxlySecondaryText)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryFrequencyToggle(selected: String, onSelect: (String) -> Unit) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            val options = listOf("Every 2 Weeks", "Once a Month")
            options.forEach { option ->
                val isSelected = selected == option
                Surface(
                    modifier = Modifier.weight(1f).fillMaxHeight().clickable { onSelect(option) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color.White else Color.Transparent,
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF1E293B) else BoxlySecondaryText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriceBreakdown(planType: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal ($planType Plan)", color = BoxlySecondaryText)
                Text("$19.99", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Shipping", color = BoxlySecondaryText)
                Text("FREE", color = BoxlyTeal, fontWeight = FontWeight.Bold)
            }
            Divider(modifier = Modifier.padding(vertical = 20.dp), color = BoxlyLightGray)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Due Today", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$19.99", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            }
        }
    }
}