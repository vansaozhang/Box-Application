package com.aeu.boxapplication.domain.model

import androidx.compose.ui.graphics.Color
data class OrderItemInfo(
    val name: String,
    val type: String,
    val price: String,
    val color: Color
)
data class OrderDetailProduct(
    val name: String,
    val type: String,
    val price: String,
    val color: Color
)

data class OrderHistoryItem(
    val id: String,
    val title: String,
    val date: String,
    val type: String,
    val price: String,
    val status: String,
    val statusBg: Color,
    val statusColor: Color,
    val imageLabel: String,
    val imageColors: List<Color>,
    val progress: Float,
    val subtotal: String,
    val total: String,
    // 2. Change this from List<OrderDetailProduct> to List<OrderItemInfo>
    val items: List<OrderItemInfo>
)
val dummyOrders = listOf(
    OrderHistoryItem(
        id = "8492-RE",
        title = "September Collection",
        date = "Sep 15, 2023",
        type = "Subscription Box",
        price = "$45.00",
        status = "Shipped",
        statusBg = Color(0xFFE6F7EE),
        statusColor = Color(0xFF1E9E62),
        imageLabel = "SEP",
        imageColors = listOf(Color(0xFFFFB26B), Color(0xFFE3741B)),
        progress = 0.6f,
        subtotal = "$45.00",
        total = "$45.00",
        items = listOf(
            OrderItemInfo("Eco-Home Box", "Monthly Sub", "$29.99", Color(0xFF1CE5D1)),
            OrderItemInfo("Organic Soap Set", "Add-on", "$15.01", Color(0xFFFFA726))
        ),
    ),
    OrderHistoryItem(
        id = "7721-BC",
        title = "Ethiopian Roast",
        date = "Sep 02, 2023",
        type = "Re-order",
        price = "$18.00",
        status = "Delivered",
        statusBg = Color(0xFFE3F2FD),
        statusColor = Color(0xFF1E88E5),
        imageLabel = "ROAST",
        imageColors = listOf(Color(0xFF1F1B16), Color(0xFF4B3D2E)),
        progress = 1.0f,
        subtotal = "$18.00",
        total = "$18.00",
        items = listOf(
            OrderItemInfo("Eco-Home Box", "Monthly Sub", "$29.99", Color(0xFF1CE5D1)),
            OrderItemInfo("Organic Soap Set", "Add-on", "$15.01", Color(0xFFFFA726))
        ),
    )
)