package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeu.boxapplication.ui.components.AppPrimaryButton

@Composable
fun CheckoutPaymentScreen(
    onBack: () -> Unit = {},
    onReviewOrder: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onShopClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val (selectedMethod, setSelectedMethod) = remember { mutableStateOf(0) }
    val (cardNumber, setCardNumber) = remember { mutableStateOf("") }
    val (expiry, setExpiry) = remember { mutableStateOf("") }
    val (cvc, setCvc) = remember { mutableStateOf("") }
    val (cardName, setCardName) = remember { mutableStateOf("") }
    val (sameBilling, setSameBilling) = remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 210.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF111827)
                    )
                }
                Text(
                    text = "Checkout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFE5E7EB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Secure",
                        tint = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            StepIndicator(currentStep = 2)

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Payment Method",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select a payment method or add a new one.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "SAVED METHODS",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodCard(
                title = "Mastercard ending in 4288",
                subtitle = "Expires 09/25",
                badge = "MC",
                selected = selectedMethod == 0,
                onSelect = { setSelectedMethod(0) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            PaymentMethodCard(
                title = "Apple Pay",
                subtitle = "Linked to your wallet",
                badge = "AP",
                selected = selectedMethod == 1,
                onSelect = { setSelectedMethod(1) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFE8F1FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CreditCard,
                                contentDescription = null,
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add New Card",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111827)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CheckoutTextField(
                        label = "CARD NUMBER",
                        value = cardNumber,
                        onValueChange = setCardNumber,
                        placeholder = "0000 0000 0000 0000",
                        keyboardType = KeyboardType.Number,
                        leadingIcon = Icons.Outlined.CreditCard,
                        trailingIcon = Icons.Outlined.Lock
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheckoutTextField(
                            label = "EXPIRY DATE",
                            value = expiry,
                            onValueChange = setExpiry,
                            placeholder = "MM/YY",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        CheckoutTextField(
                            label = "CVC",
                            value = cvc,
                            onValueChange = setCvc,
                            placeholder = "123",
                            keyboardType = KeyboardType.Number,
                            trailingIcon = Icons.Outlined.HelpOutline,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    CheckoutTextField(
                        label = "CARDHOLDER NAME",
                        value = cardName,
                        onValueChange = setCardName,
                        placeholder = "Sarah Doe"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF1F5F9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CreditCard,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Billing Address",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = "Same as shipping address",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                    Switch(
                        checked = sameBilling,
                        onCheckedChange = setSameBilling,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF1E88E5),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total to pay",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = "$102.75",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                AppPrimaryButton(
                    text = "Review Order ->",
                    onClick = onReviewOrder
                )
            }
            SubscriberBottomNav(
                selected = SubscriberBottomNavItem.Shop,
                onHomeClick = onHomeClick,
                onHistoryClick = onHistoryClick,
                onShopClick = onShopClick,
                onProfileClick = onProfileClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    val activeColor = Color(0xFF1E88E5)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepCircle(step = 1, currentStep = currentStep)
        StepLine(active = currentStep > 1)
        StepCircle(step = 2, currentStep = currentStep)
        StepLine(active = currentStep > 2)
        StepCircle(step = 3, currentStep = currentStep)
    }
    Spacer(modifier = Modifier.height(6.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Shipping", fontSize = 11.sp, color = activeColor)
        Text(text = "Payment", fontSize = 11.sp, color = activeColor)
        Text(text = "Review", fontSize = 11.sp, color = Color(0xFF6B7280))
    }
}

@Composable
private fun StepCircle(step: Int, currentStep: Int) {
    val activeColor = Color(0xFF1E88E5)
    val inactiveColor = Color(0xFFE2E8F0)
    val isCompleted = currentStep > step
    val isActive = currentStep == step
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = when {
                    isCompleted || isActive -> Color(0xFFE8F1FF)
                    else -> Color(0xFFF1F5F9)
                },
                shape = CircleShape
            )
            .border(
                width = if (isActive) 2.dp else 0.dp,
                color = if (isActive) activeColor else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = activeColor,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Text(
                text = step.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) activeColor else Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun RowScope.StepLine(active: Boolean) {
    Box(
        modifier = Modifier
            .height(2.dp)
            .weight(1f)
            .background(if (active) Color(0xFF1E88E5) else Color(0xFFE2E8F0))
    )
}

@Composable
private fun PaymentMethodCard(
    title: String,
    subtitle: String,
    badge: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF1E88E5),
                    unselectedColor = Color(0xFFCBD5E1)
                )
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun CheckoutTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )
            },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType
            ),
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            },
            trailingIcon = {
                if (trailingIcon != null) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                disabledContainerColor = Color(0xFFF8FAFC),
                focusedIndicatorColor = Color(0xFF1E88E5),
                unfocusedIndicatorColor = Color(0xFFE2E8F0),
                cursorColor = Color(0xFF1E88E5)
            )
        )
    }
}
