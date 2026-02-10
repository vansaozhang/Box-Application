package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ShopProductsScreen(
    products: List<ShopProduct>,
    cartItems: List<CartItem>,
    onProductClick: () -> Unit = {},
    onAddToCart: (ShopProduct) -> Unit = {},
    onGiftClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onShopClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var lastAddedProduct by remember { mutableStateOf<ShopProduct?>(null) }
    val cartCount = cartItems.sumOf { it.quantity }

    androidx.compose.runtime.LaunchedEffect(lastAddedProduct) {
        if (lastAddedProduct != null) {
            delay(2200)
            lastAddedProduct = null
        }
    }

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
                .padding(bottom = 84.dp)
        ) {
            ShopHeader(
                onNotificationsClick = onNotificationsClick,
                onCartClick = onCartClick,
                cartCount = cartCount
            )
            Spacer(modifier = Modifier.height(18.dp))
            ShopTitle()
            Spacer(modifier = Modifier.height(14.dp))
            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            CategoryChips()
            Spacer(modifier = Modifier.height(16.dp))
            ProductGrid(
                products = products,
                cartItems = cartItems,
                onProductClick = onProductClick,
                onAddToCart = {
                    onAddToCart(it)
                    lastAddedProduct = it
                },
                onGiftClick = onGiftClick
            )
            Spacer(modifier = Modifier.height(18.dp))
            ViewAllRow()
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (lastAddedProduct != null) {
            AddedToCartBar(
                title = lastAddedProduct?.title.orEmpty(),
                onViewCart = onCartClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(bottom = 64.dp)
            )
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

private val ShopCardHeight = 260.dp

@Composable
private fun ShopHeader(
    onNotificationsClick: () -> Unit,
    onCartClick: () -> Unit,
    cartCount: Int
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
                    text = "L",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Linna",
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
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE6E8EC), CircleShape)
                        .clickable(onClick = onCartClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color(0xFF111827),
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
                    .size(40.dp)
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
private fun ShopTitle() {
    Column {
        Text(
            text = "Shop Products",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Browse past favorites, exclusives, and gifts",
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onFilterClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = Color(0xFF9AA1AE),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = "Search for coffee, snacks, or boxes...",
                        fontSize = 12.sp,
                        color = Color(0xFF9AA1AE),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        color = Color(0xFF111827)
                    ),
                    cursorBrush = SolidColor(Color(0xFF1E88E5)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .clickable(onClick = onFilterClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter",
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryChips() {
    val categories = listOf("All Items", "Past Boxes", "Coffee", "Snacks", "Gifts")
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEachIndexed { index, label ->
            CategoryChip(
                label = label,
                selected = index == 0
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean
) {
    val background = if (selected) Color(0xFF1E88E5) else Color.White
    val textColor = if (selected) Color.White else Color(0xFF5C6B81)
    val borderColor = if (selected) Color.Transparent else Color(0xFFE6E8EC)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProductGrid(
    products: List<ShopProduct>,
    cartItems: List<CartItem>,
    onProductClick: () -> Unit,
    onAddToCart: (ShopProduct) -> Unit,
    onGiftClick: () -> Unit
) {
    val cartIds = cartItems.map { it.product.id }.toSet()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        products.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { product ->
                    if (product.isGift) {
                        GiftCardItem(
                            product = product,
                            onClick = onGiftClick,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        ProductCard(
                            product = product,
                            onClick = onProductClick,
                            onAddClick = { onAddToCart(product) },
                            isInCart = cartIds.contains(product.id),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ShopProduct,
    onClick: () -> Unit,
    onAddClick: () -> Unit,
    isInCart: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = modifier
            .border(
                width = if (isInCart) 2.dp else 0.dp,
                color = if (isInCart) Color(0xFFBFD4FF) else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
            .height(ShopCardHeight)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.linearGradient(product.imageColors),
                        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                    )
            ) {
                if (!product.badge.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(product.badgeBg ?: Color(0xFFE6F7EE))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = product.badge,
                            fontSize = 10.sp,
                            color = product.badgeText ?: Color(0xFF1E9E62),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(14.dp)
                    )
                }
                if (product.outOfStock) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Out of Stock",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.label,
                    fontSize = 10.sp,
                    color = Color(0xFF2F6BFF),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (product.oldPrice != null) {
                            Text(
                                text = formatPrice(product.oldPrice),
                                fontSize = 10.sp,
                                color = Color(0xFF9AA1AE),
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                        Text(
                            text = if (product.pricePrefix.isNullOrBlank()) {
                                formatPrice(product.price)
                            } else {
                                "${product.pricePrefix}\n${formatPrice(product.price)}"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.outOfStock) Color(0xFF9AA1AE) else Color(0xFF111827)
                        )
                    }
                    CircleActionButton(
                        enabled = !product.outOfStock && !isInCart,
                        backgroundColor = when {
                            product.outOfStock -> Color(0xFFF1F5F9)
                            isInCart -> Color(0xFF22C55E)
                            else -> Color(0xFF1E88E5)
                        },
                        contentColor = if (product.outOfStock) Color(0xFF9AA1AE) else Color.White,
                        icon = when {
                            product.outOfStock -> Icons.Outlined.NotificationsNone
                            isInCart -> Icons.Outlined.Check
                            else -> Icons.Outlined.Add
                        },
                        onClick = onAddClick
                    )
                }
            }
        }
    }
}

@Composable
private fun GiftCardItem(
    product: ShopProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF2F6BFF),
        shadowElevation = 1.dp,
        modifier = modifier
            .height(ShopCardHeight)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2C66EE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CardGiftcard,
                    contentDescription = "Gift",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = product.label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (product.pricePrefix.isNullOrBlank()) {
                        formatPrice(product.price)
                    } else {
                        "${product.pricePrefix}\n${formatPrice(product.price)}"
                    },
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 14.sp
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = "Open",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddedToCartBar(
    title: String,
    onViewCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF111827),
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF22C55E), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Added to cart",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = "View Cart",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF60A5FA),
                modifier = Modifier.clickable(onClick = onViewCart)
            )
        }
    }
}

@Composable
private fun ViewAllRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "View all 42 products",
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun CircleActionButton(
    enabled: Boolean,
    backgroundColor: Color,
    contentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Action",
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
    }
}
