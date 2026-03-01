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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            val isCompactHeight = maxHeight < 760.dp
            val horizontalPadding = if (isCompactHeight) 16.dp else 20.dp
            val verticalPadding = if (isCompactHeight) 8.dp else 12.dp
            val topGap = if (isCompactHeight) 4.dp else 8.dp
            val iconOuterSize = if (isCompactHeight) 76.dp else 92.dp
            val iconInnerSize = if (isCompactHeight) 44.dp else 54.dp
            val iconGlyphSize = if (isCompactHeight) 24.dp else 28.dp
            val titleFontSize = if (isCompactHeight) 34.sp else 42.sp
            val titleTopGap = if (isCompactHeight) 14.dp else 20.dp
            val bodyTopGap = if (isCompactHeight) 6.dp else 8.dp
            val firstCardTopGap = if (isCompactHeight) 18.dp else 24.dp
            val cardGap = if (isCompactHeight) 12.dp else 14.dp
            val buttonTopGap = if (isCompactHeight) 16.dp else 22.dp
            val bottomGap = if (isCompactHeight) 16.dp else 20.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
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

                Spacer(modifier = Modifier.height(topGap))

                Box(
                    modifier = Modifier
                        .size(iconOuterSize)
                        .background(ConfirmTint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(iconInnerSize)
                            .background(ConfirmPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(iconGlyphSize)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(titleTopGap))

                Text(
                    text = "You're in!",
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = ConfirmTitle
                )
                Spacer(modifier = Modifier.height(bodyTopGap))
                Text(
                    text = "Welcome to the Boxly Family. Your subscription is officially active and your first box is being prepared.",
                    textAlign = TextAlign.Center,
                    color = ConfirmBody,
                    fontSize = if (isCompactHeight) 13.sp else 14.sp,
                    lineHeight = if (isCompactHeight) 18.sp else 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(firstCardTopGap))

                OrderDetailsCard(isCompact = isCompactHeight)

                Spacer(modifier = Modifier.height(cardGap))

                DeliveryProgressCard(isCompact = isCompactHeight)

                Spacer(modifier = Modifier.height(buttonTopGap))

                AppPrimaryButton(
                    text = "Go to Home",
                    onClick = goToHome
                )
                Spacer(modifier = Modifier.height(bottomGap))
            }
        }
    }
}

@Composable
private fun OrderDetailsCard(isCompact: Boolean = false) {
    val cardPadding = if (isCompact) 12.dp else 14.dp
    val metricGap = if (isCompact) 10.dp else 12.dp
    val metricDividerGap = if (isCompact) 10.dp else 12.dp
    val iconSize = if (isCompact) 40.dp else 46.dp
    val iconRadius = if (isCompact) 8.dp else 10.dp

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, ConfirmStroke, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(cardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(metricGap)
            ) {
                DetailMetric(
                    label = "ORDER NUMBER",
                    value = "#SB-992831",
                    modifier = Modifier.weight(1f),
                    isCompact = isCompact
                )
                DetailMetric(
                    label = "ESTIMATED DELIVERY",
                    value = "Oct 12, 2023",
                    highlighted = true,
                    modifier = Modifier.weight(1f),
                    alignEnd = true,
                    isCompact = isCompact
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = metricDividerGap),
                color = ConfirmStroke
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(ConfirmTint, RoundedCornerShape(iconRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BOX",
                        color = ConfirmPrimary,
                        fontSize = if (isCompact) 10.sp else 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "The Wellness Box",
                        fontSize = if (isCompact) 15.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ConfirmTitle
                    )
                    Text(
                        text = "Premium Monthly Member",
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        color = ConfirmPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryProgressCard(isCompact: Boolean = false) {
    val cardPadding = if (isCompact) 12.dp else 14.dp
    val labelSize = if (isCompact) 9.sp else 10.sp
    val progressTopGap = if (isCompact) 6.dp else 8.dp
    val progressHeight = if (isCompact) 6.dp else 8.dp

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, ConfirmStroke, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(cardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ORDERED", fontSize = labelSize, fontWeight = FontWeight.Bold, color = ConfirmPrimary)
                Text("PREPARING", fontSize = labelSize, fontWeight = FontWeight.Bold, color = ConfirmBody.copy(alpha = 0.45f))
                Text("SHIPPED", fontSize = labelSize, fontWeight = FontWeight.Bold, color = ConfirmBody.copy(alpha = 0.45f))
            }
            Spacer(modifier = Modifier.height(progressTopGap))
            LinearProgressIndicator(
                progress = { 0.33f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(progressHeight),
                color = ConfirmPrimary,
                trackColor = ConfirmStroke
            )
        }
    }
}

@Composable
private fun DetailMetric(
    label: String,
    value: String,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false,
    isCompact: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            text = label,
            fontSize = if (isCompact) 9.sp else 10.sp,
            color = ConfirmBody,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = if (isCompact) 13.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlighted) ConfirmPrimary else ConfirmTitle
        )
    }
}
