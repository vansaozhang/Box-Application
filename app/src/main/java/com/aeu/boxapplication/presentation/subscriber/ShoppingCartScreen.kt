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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShoppingCartScreen(
    cartItems: List<CartItem>,
    onIncrement: (ShopProduct) -> Unit = {},
    onDecrement: (ShopProduct) -> Unit = {},
    onRemove: (ShopProduct) -> Unit = {},
    onClear: () -> Unit = {},
    onCheckout: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onShopClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val cartCount = cartItems.sumOf { it.quantity }
    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val shipping = if (cartItems.isEmpty()) 0.0 else 5.00
    val tax = if (cartItems.isEmpty()) 0.0 else 4.25
    val total = subtotal + shipping + tax

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
                .padding(bottom = 92.dp)
        ) {
            CartHeader(
                cartCount = cartCount,
                onCartClick = onCartClick,
                onNotificationsClick = onNotificationsClick
            )
            Spacer(modifier = Modifier.height(18.dp))
            CartTitle(onClear = onClear)
            Spacer(modifier = Modifier.height(12.dp))
            CartItemsList(
                items = cartItems,
                onIncrement = onIncrement,
                onDecrement = onDecrement,
                onRemove = onRemove
            )
            Spacer(modifier = Modifier.height(18.dp))
            OrderSummaryCard(
                subtotal = subtotal,
                shipping = shipping,
                tax = tax,
                total = total
            )
            Spacer(modifier = Modifier.height(18.dp))
            CheckoutButton(total = total, onClick = onCheckout)
        }

        SubscriberBottomNav(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            selected = SubscriberBottomNavItem.Shop,
            onHomeClick = onHomeClick,
            onHistoryClick = onHistoryClick,
            onShopClick = onShopClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun CartHeader(
    cartCount: Int,
    onCartClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Sarah",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "Member since 2021",
                    fontSize = 12.sp,
                    color = Color(0xFF7A8699)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF1E88E5))
                        .clickable(onClick = onCartClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                CartBadge(
                    count = cartCount,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE6E8EC), CircleShape)
                    .clickable(onClick = onNotificationsClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF111827),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun CartTitle(onClear: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Shopping Cart",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Text(
                text = "Review your items before checkout",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }
        Text(
            text = "Clear Cart",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E88E5),
            modifier = Modifier.clickable(onClick = onClear)
        )
    }
}

@Composable
private fun CartItemsList(
    items: List<CartItem>,
    onIncrement: (ShopProduct) -> Unit,
    onDecrement: (ShopProduct) -> Unit,
    onRemove: (ShopProduct) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { item ->
            CartItemCard(
                item = item,
                onIncrement = { onIncrement(item.product) },
                onDecrement = { onDecrement(item.product) },
                onRemove = { onRemove(item.product) }
            )
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(item.product.imageColors))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.product.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Remove",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onRemove)
                    )
                }
                Text(
                    text = item.product.label,
                    fontSize = 10.sp,
                    color = Color(0xFF2F6BFF),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantityControl(
                        quantity = item.quantity,
                        onIncrement = onIncrement,
                        onDecrement = onDecrement
                    )
                    Text(
                        text = formatPrice(item.product.price * item.quantity),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantityControl(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuantityButton(
            icon = Icons.Outlined.Remove,
            onClick = onDecrement
        )
        Text(
            text = quantity.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )
        QuantityButton(
            icon = Icons.Outlined.Add,
            onClick = onIncrement
        )
    }
}

@Composable
private fun QuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF1F5F9))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF111827),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    shipping: Double,
    tax: Double,
    total: Double
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow(label = "Subtotal", value = formatPrice(subtotal))
            SummaryRow(label = "Estimated Shipping", value = formatPrice(shipping))
            SummaryRow(label = "Tax", value = formatPrice(tax))
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow(
                label = "Total",
                value = formatPrice(total),
                highlight = true
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (highlight) 15.sp else 13.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            color = Color(0xFF111827)
        )
        Text(
            text = value,
            fontSize = if (highlight) 18.sp else 13.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            color = if (highlight) Color(0xFF1E88E5) else Color(0xFF111827)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun CheckoutButton(
    total: Double,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF1E88E5),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Proceed to Checkout",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = formatPrice(total),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
