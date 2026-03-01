package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage

private val ProfilePrimary = Color(0xFF1E88E5)
private val ProfileTitle = Color(0xFF2F3A4A)
private val ProfileBody = Color(0xFF7B8794)
private val ProfileStroke = Color(0xFFE3E8EF)
private val ProfileTint = Color(0xFFEAF3FF)
private val ProfileDanger = Color(0xFFE11D48)
private val ProfileDangerTint = Color(0xFFFFF1F3)

@Composable
fun ProfileScreen(
    navController: NavController,
    userName: String,      // Real name from API
    userEmail: String,     // Real email from API
    onShippingAddressClick: () -> Unit,
    onPaymentMethodsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    // State for Logout Confirmation
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 20.dp),
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
                                .border(2.dp, ProfilePrimary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp),
                            shape = CircleShape,
                            color = ProfilePrimary,
                            shadowElevation = 2.dp
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = userName, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = ProfileTitle)
                    Text(text = userEmail, color = ProfileBody, fontSize = 14.sp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Active", "3", modifier = Modifier.weight(1f))
                StatCard("Points", "1.2k", modifier = Modifier.weight(1f))
                StatCard("Boxes", "12", modifier = Modifier.weight(1f))
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = "Account Settings",
                    fontWeight = FontWeight.SemiBold,
                    color = ProfileBody,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                ProfileMenuItem(
                    icon = Icons.Outlined.LocationOn,
                    title = "Shipping Addresses",
                    onClick = onShippingAddressClick
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.Payment,
                    title = "Payment Methods",
                    onClick = onPaymentMethodsClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F1))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Log Out", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // --- Logout Confirmation Dialog ---
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirmLogout = {
                showLogoutDialog = false
                onLogoutClick()
            }
        )
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(ProfileDangerTint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = null,
                        tint = ProfileDanger,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Log out of Boxly?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileTitle
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You will need to sign in again to access your account and subscriptions.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = ProfileBody,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ProfileStroke),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = ProfileBody
                        )
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onConfirmLogout,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProfileDanger,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Log Out", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ProfileStroke)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ProfilePrimary)
            Text(text = label, fontSize = 12.sp, color = ProfileBody)
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ProfileStroke)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ProfileTint, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ProfilePrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = ProfileTitle)
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = ProfileBody.copy(alpha = 0.65f)
            )
        }
    }
}
