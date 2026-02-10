package com.aeu.boxapplication.presentation.subscriber

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
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderDetailsScreen(
    onBack: () -> Unit = {},
    onHelp: () -> Unit = {},
    onTrackPackage: () -> Unit = {}
) {
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
        ) {
            OrderDetailsTopBar(onBack = onBack, onHelp = onHelp)
            Spacer(modifier = Modifier.height(16.dp))
            OrderStatusCard(onTrackPackage = onTrackPackage)
            Spacer(modifier = Modifier.height(18.dp))
            SectionLabel(text = "ITEMS (2)")
            Spacer(modifier = Modifier.height(8.dp))
            OrderItemsCard()
            Spacer(modifier = Modifier.height(18.dp))
            SectionLabel(text = "ORDER INFORMATION")
            Spacer(modifier = Modifier.height(8.dp))
            OrderInformationCard()
            Spacer(modifier = Modifier.height(18.dp))
            OrderTotalsCard()
        }
    }
}

@Composable
private fun OrderDetailsTopBar(
    onBack: () -> Unit,
    onHelp: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF111827)
            )
        }
        Text(
            text = "Order #8492-RE",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onHelp) {
            Icon(
                imageVector = Icons.Outlined.HelpOutline,
                contentDescription = "Help",
                tint = Color(0xFF111827)
            )
        }
    }
}

@Composable
private fun OrderStatusCard(onTrackPackage: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "STATUS",
                        fontSize = 10.sp,
                        color = Color(0xFF9AA1AE)
                    )
                    Text(
                        text = "Shipped",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E88E5)
                    )
                    Text(
                        text = "Oct 24, 2023 at 9:41 AM",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFE8F1FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(6.dp)
                        .background(Color(0xFF2F6BFF), RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ordered",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "Shipped",
                    fontSize = 11.sp,
                    color = Color(0xFF1E88E5),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Delivered",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFF))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .clickable(onClick = onTrackPackage)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Track Package",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Outlined.OpenInNew,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = Color(0xFF9AA1AE),
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun OrderItemsCard() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OrderItemRow(
                title = "Ethiopian Yirgacheffe",
                subtitle = "Whole Bean - 12oz",
                quantity = 1,
                price = "$24.35",
                gradient = listOf(Color(0xFF1B1712), Color(0xFF493A2B))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            Spacer(modifier = Modifier.height(12.dp))
            OrderItemRow(
                title = "Colombian Supremo",
                subtitle = "Ground - 12oz",
                quantity = 1,
                price = "$22.00",
                gradient = listOf(Color(0xFFFBD0A7), Color(0xFFFFE3C5))
            )
        }
    }
}

@Composable
private fun OrderItemRow(
    title: String,
    subtitle: String,
    quantity: Int,
    price: String,
    gradient: List<Color>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(Brush.linearGradient(gradient), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "IMG",
                fontSize = 10.sp,
                color = Color(0xFF9AA1AE)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )
            Box(
                modifier = Modifier
                    .background(Color(0xFFEFF4FF), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Qty: $quantity",
                    fontSize = 10.sp,
                    color = Color(0xFF375DFB),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Text(
            text = price,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )
    }
}

@Composable
private fun OrderInformationCard() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow(
                icon = Icons.Outlined.LocationOn,
                title = "Shipping Address",
                value = "Jane Doe\n808 Coffee Bean Rd, Apt 4B\nSeattle, WA 98101"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(
                icon = Icons.Outlined.CreditCard,
                title = "Payment Method",
                value = "Visa ending in 4242",
                trailing = {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE6F7EE), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PAID",
                            fontSize = 9.sp,
                            color = Color(0xFF1E9E62),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(
                icon = Icons.Outlined.ReceiptLong,
                title = "Billing Address",
                value = "Same as shipping address"
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    title: String,
    value: String,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF1F5F9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF9AA1AE)
            )
            Text(
                text = value,
                fontSize = 12.sp,
                color = Color(0xFF111827),
                lineHeight = 16.sp
            )
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        }
    }
}

@Composable
private fun OrderTotalsCard() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TotalRow(label = "Subtotal", amount = "$46.35")
            Spacer(modifier = Modifier.height(8.dp))
            TotalRow(label = "Shipping", amount = "$5.00")
            Spacer(modifier = Modifier.height(8.dp))
            TotalRow(label = "Tax", amount = "$3.20")
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            Spacer(modifier = Modifier.height(12.dp))
            TotalRow(
                label = "Total",
                amount = "$54.55",
                isTotal = true
            )
        }
    }
}

@Composable
private fun TotalRow(label: String, amount: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 13.sp else 12.sp,
            fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal,
            color = Color(0xFF6B7280)
        )
        Text(
            text = amount,
            fontSize = if (isTotal) 18.sp else 12.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
            color = Color(0xFF111827)
        )
    }
}
