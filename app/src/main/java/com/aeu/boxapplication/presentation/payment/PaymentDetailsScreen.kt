package com.aeu.boxapplication.presentation.payment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeu.boxapplication.core.utils.ValidationUtils
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppNotificationPosition
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import com.aeu.boxapplication.ui.components.AppStatusTone
import com.aeu.boxapplication.ui.components.LocalAppNotificationHostState
import org.json.JSONArray
import org.json.JSONObject
import java.time.YearMonth

data class PaymentCardInput(
    val cardNumber: String,
    val cardholderName: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cvv: String
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PaymentDetailsScreen(
    onBack: () -> Unit = {},
    onPayNow: (PaymentCardInput) -> Unit = {},
    selectedPlanName: String = "Subscription",
    selectedPlanPrice: String = "$19",
    selectedPlanPeriod: String = "/mo",
    initialCardholderName: String = "",
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
    onDismissError: () -> Unit = {}
) {
    val notificationHostState = LocalAppNotificationHostState.current
    val (cardNumberField, setCardNumberField) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val (cardholderName, setCardholderName) = rememberSaveable(initialCardholderName) {
        mutableStateOf(initialCardholderName)
    }
    val (expiryDate, setExpiryDate) = remember { mutableStateOf(TextFieldValue("")) }
    val (cvv, setCvv) = remember { mutableStateOf("") }
    val cardNumber = cardNumberField.text
    val cardNumberDigits = cardNumber.filter { it.isDigit() }
    val cvvDigits = cvv.filter { it.isDigit() }
    val isCardNumberValid =
        cardNumberDigits.length == 16 && ValidationUtils.isValidCreditCard(cardNumberDigits)
    val isCardholderValid = cardholderName.trim().length >= 2
    val isExpiryValid = isValidExpiry(expiryDate.text)
    val isCvvValid = cvvDigits.length in 3..4
    val showCardNumberError = cardNumberDigits.length == 16 && !isCardNumberValid
    val showCardholderError = cardholderName.isNotEmpty() && !isCardholderValid
    val expiryError = expiryErrorMessage(expiryDate.text)
    val showExpiryError = expiryError != null
    val showCvvError = cvv.isNotEmpty() && !isCvvValid
    val isFormValid = isCardNumberValid && isCardholderValid && isExpiryValid && isCvvValid
    val whiteInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White
    )

    AppGlobalLoadingEffect(isVisible = isSubmitting)

    LaunchedEffect(errorMessage) {
        val rawError = errorMessage?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        val paymentMessage = toPaymentErrorContent(rawError)
        notificationHostState.show(
            title = paymentMessage.title,
            message = paymentMessage.message,
            tone = AppStatusTone.Error,
            label = "Payment issue",
            position = AppNotificationPosition.Top,
            onDismiss = onDismissError
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            notificationHostState.dismiss()
            onDismissError()
        }
    }

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
                .padding(bottom = 96.dp),
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
                    imageVector = Icons.Outlined.ShoppingCart,
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
                value = cardNumberField,
                onValueChange = { input ->
                    val formatted = formatCardNumberInput(input.text)
                    setCardNumberField(
                        input.copy(
                            text = formatted,
                            selection = TextRange(formatted.length)
                        )
                    )
                },
                placeholder = {
                    Text(
                        text = "4242 4242 4242 4242",
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
                colors = whiteInputColors,
                supportingText = {
                    if (showCardNumberError) {
                        Text(
                            text = "Card number looks invalid",
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
                        imageVector = Icons.Outlined.Search,
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
                colors = whiteInputColors,
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
                                imageVector = Icons.Outlined.DateRange,
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
                        colors = whiteInputColors,
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
                        onValueChange = { input ->
                            setCvv(input.filter { it.isDigit() }.take(4))
                        },
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
                        colors = whiteInputColors,
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

            TransactionSummaryCard(
                planLabel = "$selectedPlanName Plan (${selectedPlanPeriod.toBillingCycleLabel()})",
                totalPrice = formatPriceLabel(selectedPlanPrice)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "By clicking \"Pay Now\", you agree to our Terms of Service and\nPrivacy Policy.",
                fontSize = 11.sp,
                color = Color(0xFF8C99A6),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.clickable(onClick = {})

            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
        ) {
            AppPrimaryButton(
                text = if (isSubmitting) "Processing..." else "Pay Now",
                onClick = {
                    val expiryParts = parseExpiryParts(expiryDate.text) ?: return@AppPrimaryButton
                    onPayNow(
                        PaymentCardInput(
                            cardNumber = cardNumberDigits,
                            cardholderName = cardholderName.trim(),
                            expiryMonth = expiryParts.first,
                            expiryYear = expiryParts.second,
                            cvv = cvvDigits
                        )
                    )
                },
                enabled = isFormValid && !isSubmitting,
                isLoading = isSubmitting
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
private fun TransactionSummaryCard(
    planLabel: String,
    totalPrice: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, Color(0xFFE3E8EF), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            SummaryRow(label = planLabel, value = totalPrice)
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

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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

private fun formatCardNumberInput(input: String): String {
    val digits = input.filter { it.isDigit() }.take(16)
    return digits.chunked(4).joinToString(" ")
}

private fun parseExpiryParts(value: String): Pair<Int, Int>? {
    if (!value.matches(Regex("\\d{2}/\\d{2}"))) return null
    val month = value.substring(0, 2).toIntOrNull() ?: return null
    val year = value.substring(3, 5).toIntOrNull() ?: return null
    return month to (2000 + year)
}

private fun formatPriceLabel(value: String): String {
    return if (value.contains('.')) value else "$value.00"
}

private fun String.toBillingCycleLabel(): String {
    return when (this) {
        "/yr" -> "Yearly"
        "/3mo" -> "Every 3 months"
        "/2wk" -> "Every 2 weeks"
        "/wk" -> "Weekly"
        else -> "Monthly"
    }
}

private data class PaymentErrorContent(
    val title: String,
    val message: String?
)

private fun toPaymentErrorContent(rawMessage: String): PaymentErrorContent {
    val detail = extractReadablePaymentMessage(rawMessage)
        .trim()
        .trimEnd('.')

    val normalized = detail.lowercase()

    return when {
        "already have an active subscription" in normalized ||
            "already has an active subscription" in normalized -> PaymentErrorContent(
            title = "Subscription already active",
            message = "This account already has an active subscription. Manage it from Home instead of starting a new one."
        )
        "card number is incorrect" in normalized -> PaymentErrorContent(
            title = "Card number is invalid",
            message = "Check the card number and try again."
        )
        "security code is incorrect" in normalized || "cvv" in normalized -> PaymentErrorContent(
            title = "Security code is invalid",
            message = "Review the CVV and try again."
        )
        "expired" in normalized -> PaymentErrorContent(
            title = "Card has expired",
            message = "Use a different card or update the expiry date."
        )
        "insufficient funds" in normalized -> PaymentErrorContent(
            title = "Not enough funds",
            message = "Try another card or contact your bank."
        )
        "declined" in normalized -> PaymentErrorContent(
            title = "Payment was declined",
            message = "Try another card or contact your bank for more details."
        )
        "stripe price mapping is missing" in normalized -> PaymentErrorContent(
            title = "Plan billing isn't ready",
            message = "This box and billing frequency are not configured in Stripe yet. Choose another billing option or ask the admin to finish Stripe setup."
        )
        "processing error" in normalized || "try again" in normalized -> PaymentErrorContent(
            title = "Payment couldn't be completed",
            message = "Please check your details and try again in a moment."
        )
        detail.isBlank() -> PaymentErrorContent(
            title = "Payment couldn't be completed",
            message = "Please review your card details and try again."
        )
        else -> PaymentErrorContent(
            title = "Payment couldn't be completed",
            message = detail.ensureSentence()
        )
    }
}

private fun extractReadablePaymentMessage(rawMessage: String): String {
    val trimmed = rawMessage.trim()
    if (!trimmed.startsWith("{")) {
        return trimmed.substringAfter(':', trimmed)
    }

    return runCatching {
        val json = JSONObject(trimmed)
        val messageValue = json.opt("message")
        when (messageValue) {
            is JSONArray -> {
                buildList {
                    for (index in 0 until messageValue.length()) {
                        add(messageValue.optString(index))
                    }
                }.firstOrNull { it.isNotBlank() }
            }

            else -> json.optString("message")
        }
    }.getOrNull()?.takeIf { it.isNotBlank() && it != "null" }
        ?: trimmed.substringAfter(':', trimmed)
}

private fun String.ensureSentence(): String {
    val trimmed = trim()
    if (trimmed.isEmpty()) return trimmed
    val normalized = trimmed.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
    return if (normalized.endsWith('.')) normalized else "$normalized."
}
