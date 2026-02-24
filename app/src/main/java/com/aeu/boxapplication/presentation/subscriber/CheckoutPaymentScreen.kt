package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

private val CheckoutPrimary = Color(0xFF1E88E5)
private val CheckoutTitle = Color(0xFF2F3A4A)
private val CheckoutBody = Color(0xFF7B8794)
private val CheckoutStroke = Color(0xFFE3E8EF)
private val CheckoutTint = Color(0xFFEAF3FF)
private val CheckoutSuccess = Color(0xFF21C168)

@Composable
fun CheckoutPayment(navController: NavController) {
    val (selectedPayment, setSelectedPayment) = remember { mutableStateOf("visa") }
    val (isSameAsShipping, setIsSameAsShipping) = remember { mutableStateOf(true) }

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
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 92.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = CheckoutPrimary
                        )
                    }
                    Text(
                        text = "Review & Pay",
                        modifier = Modifier
                            .padding(end = 48.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CheckoutTitle
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                CheckoutSectionTitle("ORDER SUMMARY")
                OrderSummaryItem()

                Spacer(modifier = Modifier.height(18.dp))

                CheckoutSectionTitle("PAYMENT METHOD")
                PaymentMethodCard(
                    title = "Visa ending in 4242",
                    subtitle = "Expires 03/25",
                    badgeText = "VISA",
                    badgeColor = Color(0xFF1F2A44),
                    selected = selectedPayment == "visa",
                    isDefault = true,
                    onSelect = { setSelectedPayment("visa") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PaymentMethodCard(
                    title = "Mastercard ending in 8801",
                    subtitle = "Expires 02/25",
                    badgeText = "MC",
                    badgeColor = Color(0xFFE11D2A),
                    selected = selectedPayment == "mastercard",
                    isDefault = false,
                    onSelect = { setSelectedPayment("mastercard") }
                )

                Spacer(modifier = Modifier.height(18.dp))

                CheckoutSectionTitle("BILLING ADDRESS")
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.2.dp, CheckoutStroke, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Same as shipping address",
                            fontSize = 14.sp,
                            color = CheckoutTitle
                        )
                        Switch(
                            checked = isSameAsShipping,
                            onCheckedChange = setIsSameAsShipping,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = CheckoutPrimary,
                                checkedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFD8E0EA),
                                uncheckedThumbColor = Color.White,
                                uncheckedBorderColor = Color.Transparent,
                                checkedBorderColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.2.dp, CheckoutStroke, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "$39.00 / month",
                            color = CheckoutPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = CheckoutStroke)
                        Spacer(modifier = Modifier.height(8.dp))

                        PriceRow("Subtotal", "$39.00")
                        PriceRow("Shipping", "FREE", highlight = true)
                        PriceRow("Estimated Tax", "$3.12")

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = CheckoutStroke)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Total Due Today",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp,
                                    color = CheckoutTitle
                                )
                                Text(
                                    text = "RECURRING CHARGE STARTS NEXT MONTH",
                                    fontSize = 10.sp,
                                    color = CheckoutBody
                                )
                            }
                            Text(
                                text = "$42.12",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 34.sp,
                                color = CheckoutTitle
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LegalInfoBox()
                Spacer(modifier = Modifier.height(12.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                AppPrimaryButton(
                    text = "Pay & Subscribe",
                    onClick = { navController.navigate(Screen.CompletePayment.route) }
                )
            }
        }
    }
}

@Composable
private fun CheckoutSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = CheckoutTitle
    )
}

@Composable
private fun OrderSummaryItem() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, CheckoutStroke, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(CheckoutTint, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BOX",
                    fontSize = 11.sp,
                    color = CheckoutPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "The Gourmet Snack Box",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = CheckoutTitle
                )
                Text(
                    text = "Monthly Subscription Plan",
                    color = CheckoutBody,
                    fontSize = 12.sp
                )
                Text(
                    text = "$39.00 / month",
                    color = CheckoutPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    selected: Boolean,
    isDefault: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (selected) CheckoutPrimary else CheckoutStroke
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(
                onClick = onSelect,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(1.5.dp, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(CheckoutPrimary, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(badgeColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CheckoutTitle
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = CheckoutBody
                )
            }

            if (isDefault) {
                Surface(
                    color = CheckoutTint,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "DEFAULT",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CheckoutPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = CheckoutBody,
            fontSize = 14.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = if (highlight) CheckoutSuccess else CheckoutTitle
        )
    }
}

@Composable
private fun LegalInfoBox() {
    Surface(
        color = CheckoutTint,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CheckoutStroke, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = CheckoutPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "By clicking Pay & Subscribe, you agree to a monthly charge of $42.12 including taxes until you cancel. Your next billing date will be October 24, 2023.",
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = CheckoutBody
            )
        }
    }
}
