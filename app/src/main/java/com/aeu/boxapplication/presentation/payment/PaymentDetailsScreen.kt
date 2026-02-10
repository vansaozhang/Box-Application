package com.aeu.boxapplication.presentation.payment

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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import java.time.YearMonth

@Composable
fun PaymentDetailsScreen(
    onBack: () -> Unit = {},
    onPayNow: () -> Unit = {}
) {
    val (cardNumber, setCardNumber) = remember { mutableStateOf("") }
    val (cardholderName, setCardholderName) = remember { mutableStateOf("") }
    val (expiryDate, setExpiryDate) = remember { mutableStateOf(TextFieldValue("")) }
    val (cvv, setCvv) = remember { mutableStateOf("") }
    val cardNumberDigits = cardNumber.filter { it.isDigit() }
    val cvvDigits = cvv.filter { it.isDigit() }
    val isCardNumberValid = cardNumberDigits.length == 16
    val isCardholderValid = cardholderName.trim().length >= 2
    val isExpiryValid = isValidExpiry(expiryDate.text)
    val isCvvValid = cvvDigits.length in 3..4
    val showCardNumberError = cardNumber.isNotEmpty() && !isCardNumberValid
    val showCardholderError = cardholderName.isNotEmpty() && !isCardholderValid
    val expiryError = expiryErrorMessage(expiryDate.text)
    val showExpiryError = expiryError != null
    val showCvvError = cvv.isNotEmpty() && !isCvvValid
    val isFormValid = isCardNumberValid && isCardholderValid && isExpiryValid && isCvvValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                    text = "Payment Details",
                    modifier = Modifier
                        .padding(end = 48.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2F3A4A)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFFEFF5FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CreditCard,
                    contentDescription = null,
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Add Credit Card",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F3A4A)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Enter your card details to complete the subscription.",
                fontSize = 13.sp,
                color = Color(0xFF7B8794),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            FormLabel(text = "Card Number")
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { setCardNumber(it) },
                placeholder = {
                    Text(
                        text = "0000 0000 0000 0000",
                        fontSize = 12.sp,
                        color = Color(0xFF7B8794)
                    )
                },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 16.dp, height = 12.dp)
                                .background(Color(0xFF94A3B8), RoundedCornerShape(3.dp))
                        )
                    }
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1F2A44), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "VISA",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(14.dp),
                isError = showCardNumberError,
                supportingText = {
                    if (showCardNumberError) {
                        Text(
                            text = "Enter a valid 16-digit card number",
                            fontSize = 11.sp,
                            color = Color(0xFFE11D2A)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            FormLabel(text = "Cardholder Name")
            OutlinedTextField(
                value = cardholderName,
                onValueChange = { setCardholderName(it) },
                placeholder = {
                    Text(
                        text = "e.g. John Doe",
                        fontSize = 12.sp,
                        color = Color(0xFF7B8794)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CreditCard,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(14.dp),
                isError = showCardholderError,
                supportingText = {
                    if (showCardholderError) {
                        Text(
                            text = "Enter the cardholder name",
                            fontSize = 11.sp,
                            color = Color(0xFFE11D2A)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel(text = "Expiry Date")
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { newValue ->
                            val formatted = formatExpiryInput(newValue.text)
                            setExpiryDate(
                                newValue.copy(
                                    text = formatted,
                                    selection = TextRange(formatted.length)
                                )
                            )
                        },
                        placeholder = {
                            Text(
                                text = "MM/YY",
                                fontSize = 12.sp,
                                color = Color(0xFF7B8794)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(14.dp),
                        isError = showExpiryError,
                        supportingText = {
                            if (showExpiryError) {
                                Text(
                                    text = expiryError ?: "Invalid expiry date",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE11D2A)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel(text = "CVV")
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { setCvv(it) },
                        placeholder = {
                            Text(
                                text = "123",
                                fontSize = 12.sp,
                                color = Color(0xFF7B8794)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(14.dp),
                        isError = showCvvError,
                        supportingText = {
                            if (showCvvError) {
                                Text(
                                    text = "3 or 4 digits",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE11D2A)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "TRANSACTION SUMMARY",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2F3A4A)
            )
            Spacer(modifier = Modifier.height(10.dp))

            TransactionSummaryCard()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "By clicking \"Pay Now\", you agree to our Terms of Service and\nPrivacy Policy.",
                fontSize = 11.sp,
                color = Color(0xFF8C99A6),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier
                    .clickable(
                        onClick = {},
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    )
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
        ) {
            AppPrimaryButton(
                text = "Pay Now",
                onClick = onPayNow,
                enabled = isFormValid
            )
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF2F3A4A)
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun TransactionSummaryCard() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, Color(0xFFE3E8EF), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            SummaryRow(label = "Pro Plan (Monthly)", value = "$19.00")
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow(label = "Tax (0%)", value = "$0.00")
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2F3A4A)
                )
                Text(
                    text = "$19.00",
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

private fun isValidExpiry(value: String): Boolean {
    if (!value.matches(Regex("\\d{2}/\\d{2}"))) return false
    val month = value.substring(0, 2).toIntOrNull() ?: return false
    if (month !in 1..12) return false
    val year = value.substring(3, 5).toIntOrNull() ?: return false
    val fullYear = 2000 + year
    val expiry = YearMonth.of(fullYear, month)
    val current = YearMonth.now()
    return !expiry.isBefore(current)
}

private fun expiryErrorMessage(value: String): String? {
    if (value.isEmpty()) return null
    if (!value.matches(Regex("\\d{2}/\\d{2}"))) return "Use MM/YY"
    val month = value.substring(0, 2).toIntOrNull() ?: return "Use MM/YY"
    if (month !in 1..12) return "Enter a valid month"
    val year = value.substring(3, 5).toIntOrNull() ?: return "Use MM/YY"
    val fullYear = 2000 + year
    val expiry = YearMonth.of(fullYear, month)
    val current = YearMonth.now()
    return if (expiry.isBefore(current)) "Card expired" else null
}

private fun formatExpiryInput(input: String): String {
    val digits = input.filter { it.isDigit() }.take(4)
    return if (digits.length <= 2) {
        digits
    } else {
        "${digits.substring(0, 2)}/${digits.substring(2)}"
    }
}
