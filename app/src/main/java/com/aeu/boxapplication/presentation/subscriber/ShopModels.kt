package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.ui.graphics.Color
import java.util.Locale

data class ShopProduct(
    val id: String,
    val label: String,
    val title: String,
    val price: Double,
    val imageColors: List<Color>,
    val oldPrice: Double? = null,
    val badge: String? = null,
    val badgeBg: Color? = null,
    val badgeText: Color? = null,
    val outOfStock: Boolean = false,
    val isGift: Boolean = false,
    val pricePrefix: String? = null
)

data class CartItem(
    val product: ShopProduct,
    val quantity: Int
)

fun formatPrice(amount: Double): String =
    "$" + String.format(Locale.US, "%.2f", amount)

fun demoShopProducts(): List<ShopProduct> = listOf(
    ShopProduct(
        id = "sept_collection",
        label = "BUNDLE",
        title = "September Collection Box",
        price = 45.00,
        oldPrice = 55.00,
        imageColors = listOf(Color(0xFFFFB26B), Color(0xFFE3741B))
    ),
    ShopProduct(
        id = "ethiopian_roast",
        label = "COFFEE",
        title = "Ethiopian Yirgacheffe Roast",
        price = 18.00,
        badge = "Best Seller",
        badgeBg = Color(0xFFE6F7EE),
        badgeText = Color(0xFF1E9E62),
        imageColors = listOf(Color(0xFF1F1B16), Color(0xFF4B3D2E))
    ),
    ShopProduct(
        id = "dark_chocolate",
        label = "SNACKS",
        title = "Dark Chocolate Variety Pack",
        price = 12.50,
        imageColors = listOf(Color(0xFF3A1F1A), Color(0xFF6B2A1C))
    ),
    ShopProduct(
        id = "august_collection",
        label = "BUNDLE",
        title = "August Summer Collection",
        price = 45.00,
        imageColors = listOf(Color(0xFF2F2F2F), Color(0xFF5A5A5A))
    ),
    ShopProduct(
        id = "july_collection",
        label = "BUNDLE",
        title = "July Collection Box",
        price = 45.00,
        imageColors = listOf(Color(0xFFBDBDBD), Color(0xFFE0E0E0)),
        outOfStock = true
    ),
    ShopProduct(
        id = "gift_card",
        label = "GIFTS",
        title = "Digital Gift Card",
        price = 25.00,
        pricePrefix = "From",
        isGift = true,
        imageColors = listOf(Color(0xFF2F6BFF), Color(0xFF1B4FD6))
    )
)
