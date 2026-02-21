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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.presentation.navigation.Screen

// Colors based on image
val BoxlyTeal = Color(0xFF1CE5D1)
val BoxlyBackground = Color(0xFFF8FAFB)
val BoxlyDarkText = Color(0xFF1A1C1E)

@Composable
fun ShopProductsScreen(navController: NavController) {
    Scaffold(
        topBar = { TopHeader() },
        bottomBar = { /* Implement Bottom Navigation here */ }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BoxlyBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            SearchBarSection()

            // Categories
            CategoryChipsSection()

            // Main Featured Card
            FeaturedBoxCard()

            // List Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Discover All Boxes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoxlyDarkText
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sort by", color = BoxlyTeal, fontSize = 14.sp)
                    Icon(Icons.Default.Star, contentDescription = null, tint = BoxlyTeal, modifier = Modifier.size(14.dp))
                }
            }

            // Subscription List
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
                    .background(BoxlyTeal, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Boxly", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
        Row {
            Icon(Icons.Outlined.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.Notifications, contentDescription = null)
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
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(25.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEFF3F6),
            unfocusedContainerColor = Color(0xFFEFF3F6),
            unfocusedBorderColor = Color.Transparent
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
                color = if (isSelected) BoxlyTeal else Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    color = if (isSelected) Color.White else Color.Gray,
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
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Placeholder for the background image + gradient
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFF8B6E5E), Color(0xFF1A1C1E)))
            ))

            Column(modifier = Modifier.padding(24.dp)) {
                Surface(
                    color = BoxlyTeal,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "PICK OF THE MONTH",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("The Wellness Box", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Curated for your peace of mind. Includes 6 organic self-care essentials.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "$34.99/mo", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Get Started", color = Color.Black, fontWeight = FontWeight.Bold)
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
        color = Color.White
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F5F9))) {
                // AsyncImage would go here
                if (badge != null) {
                    Surface(
                        color = BoxlyTeal.copy(alpha = 0.8f),
                        modifier = Modifier.padding(4.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(badge, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = BoxlyTeal, modifier = Modifier.size(14.dp))
                        Text(rating, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(desc, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$price", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("/mo", fontSize = 10.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.SubscribDetail.route)
                        }
                    ) {
                        Text("View Details", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}