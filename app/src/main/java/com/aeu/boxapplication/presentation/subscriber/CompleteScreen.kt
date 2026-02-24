package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.ui.components.AppPrimaryButton

private val ConfirmPrimary = Color(0xFF1E88E5)
private val ConfirmTitle = Color(0xFF2F3A4A)
private val ConfirmBody = Color(0xFF7B8794)
private val ConfirmStroke = Color(0xFFE3E8EF)
private val ConfirmTint = Color(0xFFEAF3FF)

@Composable
fun CompletePayment(navController: NavController) {
    val goToHome = {
        navController.navigate(Screen.ShopProducts.route) {
            popUpTo(Screen.CompletePayment.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(40.dp))
                    Text(
                        text = "ORDER CONFIRMED",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ConfirmBody
                    )
                    IconButton(onClick = goToHome) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ConfirmPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(ConfirmTint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(ConfirmPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "You're in!",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ConfirmTitle
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome to the Boxly Family. Your subscription is officially active and your first box is being prepared.",
                    textAlign = TextAlign.Center,
                    color = ConfirmBody,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OrderDetailsCard()

                Spacer(modifier = Modifier.height(14.dp))

                DeliveryProgressCard()

                Spacer(modifier = Modifier.height(22.dp))

                AppPrimaryButton(
                    text = "Go to Home",
                    onClick = goToHome
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun OrderDetailsCard() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, ConfirmStroke, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailMetric(
                    label = "ORDER NUMBER",
                    value = "#SB-992831"
                )
                DetailMetric(
                    label = "ESTIMATED DELIVERY",
                    value = "Oct 12, 2023",
                    highlighted = true
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = ConfirmStroke
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(ConfirmTint, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BOX",
                        color = ConfirmPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "The Wellness Box",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ConfirmTitle
                    )
                    Text(
                        text = "Premium Monthly Member",
                        fontSize = 12.sp,
                        color = ConfirmPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryProgressCard() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, ConfirmStroke, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ORDERED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ConfirmPrimary)
                Text("PREPARING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ConfirmBody.copy(alpha = 0.45f))
                Text("SHIPPED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ConfirmBody.copy(alpha = 0.45f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = 0.33f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = ConfirmPrimary,
                trackColor = ConfirmStroke
            )
        }
    }
}

@Composable
private fun DetailMetric(label: String, value: String, highlighted: Boolean = false) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            color = ConfirmBody,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlighted) ConfirmPrimary else ConfirmTitle
        )
    }
}
