package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
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
import coil.compose.AsyncImage
import com.aeu.boxapplication.presentation.navigation.Screen

private val DetailsPrimary = Color(0xFF1E88E5)
private val DetailsTitle = Color(0xFF2F3A4A)
private val DetailsBody = Color(0xFF7B8794)
private val DetailsStroke = Color(0xFFE3E8EF)
private val DetailsTint = Color(0xFFEAF3FF)
private val DetailsBackground = Color(0xFFF8FAFB)

@Composable
fun SubscriptionDetailsScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.White,
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DetailsPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Subscribe Now", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
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
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, DetailsStroke),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                        Brush.verticalGradient(listOf(Color.Transparent, DetailsTitle.copy(alpha = 0.72f)))
                    ))

                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Surface(color = DetailsTint, shape = RoundedCornerShape(8.dp)) {
                            Text("ACTIVE", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DetailsPrimary)
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
                    Text("$29.99", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DetailsPrimary)
                    Text("/mo", color = DetailsBody, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = DetailsPrimary, modifier = Modifier.size(16.dp))
                    Text(" 4.9 ", fontWeight = FontWeight.Bold, color = DetailsTitle)
                    Text("(1.2k)", color = DetailsBody)
                }
            }

            // 4. Shipping Address
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Shipping Address", fontWeight = FontWeight.Bold, color = DetailsTitle)
                Row(modifier = Modifier.clickable {  }, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = DetailsPrimary, modifier = Modifier.size(14.dp))
                    Text(" Edit", color = DetailsPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                color = DetailsBackground,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, DetailsStroke)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(45.dp).background(DetailsTint, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = DetailsPrimary)
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Home Office", fontWeight = FontWeight.Bold, color = DetailsTitle)
                        Text("123 Serenity Lane, Wellness District\nSan Francisco, CA 94105", fontSize = 12.sp, color = DetailsBody)
                    }
                }
            }

            // 5. Sneak Peek
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sneak Peek: What's Inside", fontWeight = FontWeight.Bold, color = DetailsTitle)
                Text("View All", color = DetailsBody, fontSize = 12.sp)
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
        border = BorderStroke(1.dp, if (isSelected) DetailsPrimary else DetailsStroke),
        color = if (isSelected) Color.White else Color.Transparent
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DetailsTitle)
            Text(subtitle, fontSize = 10.sp, color = DetailsBody)
        }
    }
}

@Composable
fun InsideItem(title: String, category: String) {
    Column(modifier = Modifier.padding(end = 16.dp)) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(DetailsBackground)
                .border(1.dp, DetailsStroke, RoundedCornerShape(28.dp))
        )
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp), fontSize = 12.sp, color = DetailsTitle)
        Text(category, color = DetailsBody, fontSize = 11.sp)
    }
}
