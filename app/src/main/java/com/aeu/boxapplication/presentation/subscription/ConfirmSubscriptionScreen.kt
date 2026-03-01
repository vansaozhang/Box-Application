package com.aeu.boxapplication.presentation.subscription

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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeu.boxapplication.ui.components.AppPrimaryButton

@Composable
fun ConfirmSubscriptionScreen(
    onBack: () -> Unit = {},
    onEditPlan: () -> Unit = {},
    onConfirmPay: () -> Unit = {},
    selectedPlanName: String = "Pro",
    selectedPlanPrice: String = "$19",
    selectedPlanPeriod: String = "/mo",
    selectedPlanFeatures: List<String> = listOf(
        "Unlimited recurring orders",
        "Advanced analytics",
        "Free shipping on all orders"
    ),
    isSubmitting: Boolean = false,
    errorMessage: String? = null
) {
    val methods = listOf(
        PaymentMethodUi(
            id = "stripe_card",
            title = "Card via Stripe",
            subtitle = "Secure test payment",
            badgeText = "STRIPE",
            badgeColor = Color(0xFF635BFF)
        )
    )

    Scaffold(
        containerColor = Color.White
    ) {
        paddingValues ->
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
                    .padding(bottom = 88.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E88E5)
                        )
                    }
                    Text(
                        text = "Confirm Subscription",
                        modifier = Modifier
                            .padding(end = 48.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2F3A4A)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Review Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F3A4A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Review your selection before confirming.",
                    fontSize = 13.sp,
                    color = Color(0xFF7B8794),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                SelectedPlanCard(
                    onEdit = onEditPlan,
                    planName = selectedPlanName,
                    planPrice = selectedPlanPrice,
                    planPeriod = selectedPlanPeriod,
                    features = selectedPlanFeatures
                )

                Spacer(modifier = Modifier.height(18.dp))

                SectionTitle(text = "PAYMENT METHOD")
                Spacer(modifier = Modifier.height(10.dp))

                methods.forEach { method ->
                    PaymentMethodRow(
                        method = method,
                        selected = true,
                        onSelect = {}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))

                SectionTitle(text = "ORDER SUMMARY")
                Spacer(modifier = Modifier.height(10.dp))

                OrderSummaryCard(totalPrice = selectedPlanPrice)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your subscription will renew automatically based on your selected ${if (selectedPlanPeriod == "/yr") "yearly" else "monthly"} cycle.\nYou can cancel anytime in your settings.",
                    fontSize = 11.sp,
                    color = Color(0xFF8C99A6),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                AppPrimaryButton(
                    text = if (isSubmitting) "Processing..." else "Confirm & Subscribe",
                    onClick = onConfirmPay,
                    enabled = !isSubmitting
                )
            }
        }
    }
}

@Composable
private fun SelectedPlanCard(
    onEdit: () -> Unit,
    planName: String,
    planPrice: String,
    planPeriod: String,
    features: List<String>
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Color(0xFF1E88E5), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECTED PLAN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E88E5)
                )
                Text(
                    text = "Edit",
                    fontSize = 12.sp,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.clickable(
                        onClick = onEdit,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = planName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F3A4A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = planPrice,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F3A4A)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = planPeriod,
                    fontSize = 12.sp,
                    color = Color(0xFF7B8794)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider(color = Color(0xFFE3E8EF))

            Spacer(modifier = Modifier.height(10.dp))

            features.forEachIndexed { index, feature ->
                FeatureLine(text = feature)
                if (index != features.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FeatureLine(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(Color(0xFF1E88E5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF2F3A4A)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF2F3A4A)
    )
}

private data class PaymentMethodUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val badgeColor: Color
)

@Composable
private fun PaymentMethodRow(
    method: PaymentMethodUi,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (selected) Color(0xFF1E88E5) else Color(0xFFE3E8EF)
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
                    .size(42.dp)
                    .background(method.badgeColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = method.badgeText,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2F3A4A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = method.subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF7B8794)
                )
            }

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
                            .background(Color(0xFF1E88E5), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(totalPrice: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, Color(0xFFE3E8EF), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            SummaryRow(label = "Subtotal", value = totalPrice)
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow(label = "Tax (0%)", value = "$0.00")
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFE3E8EF))
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Due Today",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2F3A4A)
                )
                Text(
                    text = totalPrice,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5)
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF7B8794)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color(0xFF2F3A4A)
        )
    }
}
