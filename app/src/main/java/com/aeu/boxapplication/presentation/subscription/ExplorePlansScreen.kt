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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeu.boxapplication.ui.components.AppPrimaryButton

@Composable
fun ExplorePlansScreen(
    onBack: () -> Unit = {},
    onRestorePurchases: () -> Unit = {},
    onSelectStarter: () -> Unit = {},
    onSelectPro: () -> Unit = {},
    onSelectBusiness: () -> Unit = {}
) {
    val (isMonthly, setIsMonthly) = remember { mutableStateOf(true) }

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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E88E5)
                        )
                    }
                    Text(
                        text = "Explore Plans",
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
                    text = "Unlock Premium Features",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F3A4A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Choose a plan that works best for your shopping habits.",
                    fontSize = 13.sp,
                    color = Color(0xFF7B8794),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                BillingToggle(
                    isMonthly = isMonthly,
                    onMonthlyClick = { setIsMonthly(true) },
                    onYearlyClick = { setIsMonthly(false) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PlanCard(
                    title = "Starter",
                    subtitle = "FOR INDIVIDUALS",
                    price = "$9",
                    period = "/mo",
                    features = listOf(
                        PlanFeature("5 recurring orders", true),
                        PlanFeature("Basic analytics", true),
                        PlanFeature("Free shipping", false)
                    ),
                    buttonText = "Select Starter",
                    onSelect = onSelectStarter
                )

                Spacer(modifier = Modifier.height(16.dp))

                PlanCard(
                    title = "Pro",
                    subtitle = "FOR POWER USERS",
                    price = "$19",
                    period = "/mo",
                    features = listOf(
                        PlanFeature("Unlimited recurring orders", true),
                        PlanFeature("Advanced analytics", true),
                        PlanFeature("Free shipping on all orders", true),
                        PlanFeature("Priority support", true)
                    ),
                    buttonText = "Select Pro",
                    highlight = true,
                    badge = "MOST POPULAR",
                    onSelect = onSelectPro
                )

                Spacer(modifier = Modifier.height(16.dp))

                PlanCard(
                    title = "Business",
                    subtitle = "FOR TEAMS",
                    price = "$49",
                    period = "/mo",
                    features = listOf(
                        PlanFeature("Everything in Pro", true),
                        PlanFeature("Multiple user seats", true),
                        PlanFeature("Dedicated account manager", true)
                    ),
                    buttonText = "Select Business",
                    onSelect = onSelectBusiness
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Restore Purchases",
                    fontSize = 13.sp,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable(
                            onClick = onRestorePurchases,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Recurring billing, cancel anytime. By\nsubscribing you agree to our Terms of Service.",
                    fontSize = 11.sp,
                    color = Color(0xFF9AA6B2),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun BillingToggle(
    isMonthly: Boolean,
    onMonthlyClick: () -> Unit,
    onYearlyClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFFF1F5F9), shape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TogglePill(
                text = "Monthly",
                selected = isMonthly,
                onClick = onMonthlyClick
            )
            TogglePill(
                text = "Yearly",
                selected = !isMonthly,
                onClick = onYearlyClick
            )
        }
        Surface(
            color = Color(0xFF21C168),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 80.dp, y = (-12).dp)
                .rotate(8f)
        ) {
            Text(
                text = "SAVE 20%",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)
            )
        }
    }
}

@Composable
private fun TogglePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .background(if (selected) Color.White else Color.Transparent, shape)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color(0xFF2F3A4A) else Color(0xFF7B8794)
        )
    }
}

private data class PlanFeature(
    val text: String,
    val included: Boolean
)

@Composable
private fun PlanCard(
    title: String,
    subtitle: String,
    price: String,
    period: String,
    features: List<PlanFeature>,
    buttonText: String,
    onSelect: () -> Unit,
    highlight: Boolean = false,
    badge: String? = null
) {
    val borderColor = if (highlight) Color(0xFF3B82F6) else Color(0xFFE3EAF2)
    val background = if (highlight) Color(0xFFF5F9FF) else Color.White

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (badge != null) {
            Surface(
                color = Color(0xFF1E88E5),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (badge != null) 14.dp else 0.dp)
                .background(background, RoundedCornerShape(18.dp))
                .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2F3A4A)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = Color(0xFF7B8794)
                    )
                }
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = price,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2F3A4A)
                    )
                    Text(
                        text = period,
                        fontSize = 12.sp,
                        color = Color(0xFF7B8794),
                        modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE9EFF6))
            )
            Spacer(modifier = Modifier.height(10.dp))

            features.forEach { feature ->
                FeatureItem(
                    text = feature.text,
                    included = feature.included,
                    highlight = highlight && feature.included
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppPrimaryButton(
                text = buttonText,
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = true
            )
        }
    }
}

@Composable
private fun FeatureItem(
    text: String,
    included: Boolean,
    highlight: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconColor = if (included) Color(0xFF1E88E5) else Color(0xFF9AA6B2)
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(
                    if (included) Color(0xFFEAF3FF) else Color(0xFFF1F5F9),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (included) Icons.Outlined.Check else Icons.Outlined.Close,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
            color = if (included) Color(0xFF2F3A4A) else Color(0xFF9AA6B2)
        )
    }
}
