package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun CompletePayment(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB))
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(48.dp))
            Text("ORDER CONFIRMED", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            IconButton(onClick = { navController.navigate("shop_discover") }) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Animation/Icon Area
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(BoxlyTeal.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = BoxlyTeal
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "You’re in!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Welcome to the Boxly Family. Your subscription is officially active and your first box is being prepared.",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Order Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DetailColumn("ORDER NUMBER", "#SB-992831")
                        DetailColumn("ESTIMATED DELIVERY", "Oct 12, 2023", isTeal = true)
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1589710780350-1215889378f1?w=400",
                            contentDescription = null,
                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("The Wellness Box", fontWeight = FontWeight.Bold)
                            Text("Premium Monthly Member", fontSize = 12.sp, color = BoxlyTeal)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Tracker Mockup
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ORDERED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BoxlyTeal)
                        Text("PREPARING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                        Text("SHIPPED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = 0.33f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = BoxlyTeal,
                        trackColor = Color(0xFFF1F5F9)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = { navController.navigate("shop_discover") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text("Go to Home", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text("Share the excitement", color = Color.Gray, fontSize = 14.sp)
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SocialCircle(Icons.Default.Share)
                SocialCircle(Icons.Default.Share) // Replace with IG icon
                SocialCircle(Icons.Default.Share) // Replace with FB icon
            }

            TextButton(onClick = { /* Contact Support */ }) {
                Text("Need help? Contact Support", color = BoxlySecondaryText, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DetailColumn(label: String, value: String, isTeal: Boolean = false) {
    Column {
        Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isTeal) BoxlyTeal else Color.Black)
    }
}

@Composable
fun SocialCircle(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier.size(50.dp),
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}