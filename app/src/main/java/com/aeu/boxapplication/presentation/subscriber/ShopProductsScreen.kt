package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.presentation.navigation.Screen

// Colors based on image
val BoxlyTeal = Color(0xFF1CE5D1)
val BoxlyBackground = Color(0xFFF8FAFB)
val BoxlyDarkText = Color(0xFF1A1C1E)
private val ShopPrimary = Color(0xFF1E88E5)
private val ShopTitle = Color(0xFF2F3A4A)
private val ShopBody = Color(0xFF7B8794)
private val ShopStroke = Color(0xFFE3E8EF)
private val ShopTint = Color(0xFFEAF3FF)

@Composable
fun ShopProductsScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.White,
        topBar = { TopHeader() },
        bottomBar = { /* Implement Bottom Navigation here */ }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                SearchBarSection()
                CategoryChipsSection()
                FeaturedBoxCard()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discover All Boxes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopTitle
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sort by", color = ShopPrimary, fontSize = 14.sp)
                        Icon(Icons.Default.Star, contentDescription = null, tint = ShopPrimary, modifier = Modifier.size(14.dp))
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SubscriptionItem(
                        title = "Eco-Home Essentials",
                        desc = "Green living made simple with zero-waste home supplies.",
                        price = "$29.99",
                        rating = "4.9",
                        badge = "BEST VALUE",
                        navController
                    )
                    SubscriptionItem(
                        title = "Gamer's Loot",
                        desc = "Top-tier peripherals, accessories, and fuel for late-night sessions.",
                        price = "$45.00",
                        rating = "4.8",
                        badge = "POPULAR",
                        navController
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(ShopPrimary, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Boxly", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = ShopTitle)
        }
        Row {
            Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = ShopTitle)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.Notifications, contentDescription = null, tint = ShopTitle)
        }
    }
}

@Composable
fun SearchBarSection() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        placeholder = { Text("Search boxes, brands, or categories...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ShopBody) },
        shape = RoundedCornerShape(25.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = ShopStroke,
            unfocusedBorderColor = ShopStroke
        )
    )
}

@Composable
fun CategoryChipsSection() {
    val categories = listOf("All", "Beauty", "Tech", "Snacks", "Wellness")
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        categories.forEach { name ->
            val isSelected = name == "All"
            Surface(
                modifier = Modifier.padding(end = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) ShopPrimary else Color.White,
                border = BorderStroke(1.dp, ShopStroke)
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    color = if (isSelected) Color.White else ShopBody,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FeaturedBoxCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShopStroke),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                ShopPrimary.copy(alpha = 0.95f),
                                ShopTitle
                            )
                        )
                    )
            ) {
                Surface(
                    color = ShopTint,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 20.dp, top = 16.dp)
                ) {
                    Text(
                        "PICK OF THE MONTH",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopPrimary
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        "The Wellness Box",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    "Curated for your peace of mind. Includes 6 organic self-care essentials.",
                    color = ShopBody,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$34.99",
                            color = ShopTitle,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "/mo",
                            color = ShopBody,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 3.dp)
                        )
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ShopPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Get Started", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionItem(title: String, desc: String, price: String, rating: String, badge: String? = null, navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ShopStroke)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F5F9))) {
                // AsyncImage would go here
                if (badge != null) {
                    Surface(
                        color = ShopPrimary.copy(alpha = 0.18f),
                        modifier = Modifier.padding(4.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            badge,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopPrimary,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ShopTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = ShopPrimary, modifier = Modifier.size(14.dp))
                        Text(rating, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShopTitle)
                    }
                }
                Text(desc, fontSize = 12.sp, color = ShopBody, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$price", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ShopTitle)
                    Text("/mo", fontSize = 10.sp, color = ShopBody)
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ShopTint,
                        border = BorderStroke(1.dp, ShopStroke),
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.SubscribDetail.route)
                        }
                    ) {
                        Text(
                            "View Details",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            color = ShopPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
