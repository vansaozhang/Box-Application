package com.aeu.boxapplication.presentation.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressScreen(navController: NavController) {
    // We use a Column as the root and set the background color manually
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB)) // Moved from Scaffold containerColor
            .statusBarsPadding() // Ensures the UI doesn't go under the system status bar
    ) {
        // Manual Top Bar implementation
        CenterAlignedTopAppBar(
            title = { Text("Shipping Address", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Handle Add New Address */ }) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add", tint = Color(0xFF1CE5D1))
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF8FAFB))
        )

        // Content Area
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Saved Addresses",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Example Address 1: Default
            AddressCard(
                label = "Home",
                fullName = "Sarah Jenkins",
                address = "123 Sunshine Boulevard, Apt 4B\nLos Angeles, CA 90001",
                phoneNumber = "+1 234 567 890",
                isDefault = true,
                onEditClick = { /* Navigate to Edit */ }
            )

            // Example Address 2: Work
            AddressCard(
                label = "Office",
                fullName = "Sarah Jenkins",
                address = "456 Corporate Way, Level 12\nSanta Monica, CA 90401",
                phoneNumber = "+1 098 765 432",
                isDefault = false,
                onEditClick = { /* Navigate to Edit */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Add New Button at bottom
            OutlinedButton(
                onClick = { /* Add Address */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF1CE5D1))
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = Color(0xFF1CE5D1))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Address", color = Color(0xFF1CE5D1), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp)) // Bottom padding for scroll
        }
    }
}

@Composable
fun AddressCard(
    label: String,
    fullName: String,
    address: String,
    phoneNumber: String,
    isDefault: Boolean,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = if (isDefault) BorderStroke(2.dp, Color(0xFF1CE5D1)) else null,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFF1F5F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (label == "Home") Icons.Outlined.Home else Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = label, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                if (isDefault) {
                    Surface(
                        color = Color(0xFFE0F9F6),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "DEFAULT",
                            color = Color(0xFF1CE5D1),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = fullName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(
                text = address,
                color = Color.Gray,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(text = phoneNumber, color = Color.Gray, fontSize = 13.sp)

            HorizontalDivider( // Replaced deprecated Divider with HorizontalDivider
                modifier = Modifier.padding(vertical = 12.dp),
                color    = Color(0xFFF1F5F9)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditClick) {
                    Text("Edit", color = Color(0xFF1CE5D1), fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { /* Delete logic */ }) {
                    Text("Delete", color = Color.LightGray)
                }
            }
        }
    }
}