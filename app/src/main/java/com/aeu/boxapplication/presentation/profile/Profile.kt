package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB)) // Matching background from Shop screen
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 40.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF1CE5D1), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    // Edit Badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp),
                        shape = CircleShape,
                        color = Color(0xFF1CE5D1),
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Sarah Jenkins", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = "sarah.j@example.com", color = Color.Gray, fontSize = 14.sp)
            }
        }

        // 2. Subscription Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Active", "3", modifier = Modifier.weight(1f))
            StatCard("Points", "1.2k", modifier = Modifier.weight(1f))
            StatCard("Boxes", "12", modifier = Modifier.weight(1f))
        }

        // 3. Settings Sections
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Account Settings", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))

            ProfileMenuItem(Icons.Outlined.Person, "Personal Information") { }
            ProfileMenuItem(Icons.Outlined.Add, "Payment Methods") { }
            ProfileMenuItem(Icons.Outlined.LocationOn, "Shipping Addresses") { }
            ProfileMenuItem(Icons.Outlined.Notifications, "Notifications") { }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Support", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))

            ProfileMenuItem(Icons.Outlined.Info, "Help Center") { }
            ProfileMenuItem(Icons.Outlined.Lock, "Privacy Policy") { }

            // Logout Button
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* Handle Logout */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F1))
            ) {
                Text("Log Out", color = Color.Red, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2926))
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF8FAFB), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF1CE5D1), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            Icon(Icons.Outlined.Warning, contentDescription = null, tint = Color.LightGray)
        }
    }
}