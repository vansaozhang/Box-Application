package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
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
import coil.compose.AsyncImage
import com.aeu.boxapplication.presentation.navigation.Screen

// Using the brand teal from the image

@Composable
fun SubscriptionDetailsScreen(navController: NavController) {
    var selectedFrequency by remember { mutableStateOf("Monthly") }

    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Subscription Details", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = null)
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { }) {
//                        Icon(Icons.Default.Share, contentDescription = null)
//                    }
//                }
//            )
//        },
        bottomBar = {
            Box(modifier = Modifier.padding(20.dp)) {
                Button(
                    onClick = {
                        navController.navigate(Screen.OrderConfirmed.route)
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BoxlyTeal)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Subscribe Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                shape = RoundedCornerShape(32.dp)
            ) {
                Box {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=800",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient for text readability
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.6f)))
                    ))

                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Surface(color = BoxlyTeal, shape = RoundedCornerShape(8.dp)) {
                            Text("ACTIVE", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("The Wellness Box", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. Price and Rating
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$29.99", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = BoxlyTeal)
                    Text("/mo", color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp)) // Replace with Star
                    Text(" 4.9 ", fontWeight = FontWeight.Bold)
                    Text("(1.2k)", color = Color.LightGray)
                }
            }

            // 4. Shipping Address
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Shipping Address", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.clickable {  }, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = BoxlyTeal, modifier = Modifier.size(14.dp))
                    Text(" Edit", color = BoxlyTeal, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                color = Color(0xFFF8FAFB),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(45.dp).background(BoxlyTeal.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = BoxlyTeal)
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Home Office", fontWeight = FontWeight.Bold)
                        Text("123 Serenity Lane, Wellness District\nSan Francisco, CA 94105", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // 5. Sneak Peek
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sneak Peek: What's Inside", fontWeight = FontWeight.Bold)
                Text("View All", color = Color.LightGray, fontSize = 12.sp)
            }
            Row(modifier = Modifier.padding(20.dp).horizontalScroll(rememberScrollState())) {
                InsideItem("Lavender Oil", "Essential Scent")
                InsideItem("Eco-Cork Mat", "Fitness Gear")
                InsideItem("Nightly Detox", "Herbal Tea")
            }
            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom button
        }
    }
}

@Composable
fun FrequencyOption(title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(110.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSelected) BoxlyTeal else Color(0xFFF1F5F9)),
        color = if (isSelected) Color.White else Color.Transparent
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun InsideItem(title: String, category: String) {
    Column(modifier = Modifier.padding(end = 16.dp)) {
        Box(modifier = Modifier.size(130.dp).clip(RoundedCornerShape(28.dp)).background(Color(0xFFF1F5F9)))
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp), fontSize = 12.sp)
        Text(category, color = Color.Gray, fontSize = 11.sp)
    }
}