package com.aeu.boxapplication.presentation.subscriber

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppStatusBanner
import com.aeu.boxapplication.ui.components.AppStatusTone

private val ProfilePrimary = Color(0xFF1E88E5)
private val ProfileTitle = Color(0xFF2F3A4A)
private val ProfileBody = Color(0xFF7B8794)
private val ProfileStroke = Color(0xFFE3E8EF)
private val ProfileDanger = Color(0xFFE11D48)
private val ProfileDangerTint = Color(0xFFFFF1F3)

@Composable
fun ProfileScreen(
    viewModel: SubscriberProfileViewModel,
    activeSubscriptionCount: Int,
    onShippingAddressClick: () -> Unit,
    onSubscriptionDetailsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState = viewModel.uiState
    val profile = uiState.profile
    val context = LocalContext.current
    val displayName = profile?.name?.takeIf { !it.isNullOrBlank() }
        ?: "Subscriber"
    val displayEmail = profile?.email?.takeIf { it.isNotBlank() }
    val displayPhone = profile?.phoneNumber?.takeIf { it.isNotBlank() }
    val profileImageUrl = profile?.profileImageUrl?.takeIf { it.isNotBlank() }
    val primaryContact = displayPhone ?: displayEmail ?: "No contact information on file"
    val initials = displayName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }
        .ifBlank { "BX" }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { selectedImage ->
        if (selectedImage != null) {
            viewModel.uploadProfileImage(context.contentResolver, selectedImage)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    AppGlobalLoadingEffect(isVisible = uiState.isLoading && profile == null)

    androidx.compose.material3.Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PROFILE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfilePrimary
                    )
                    Text(
                        text = "Your account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfileTitle
                    )
                }
                IconButton(onClick = { viewModel.loadProfile(forceRefresh = true) }) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Refresh profile",
                        tint = ProfileTitle
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ProfilePrimary.copy(alpha = 0.12f), CircleShape)
                        .border(2.dp, ProfilePrimary, CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUrl != null) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "$displayName profile image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = initials,
                            color = ProfilePrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        )
                    }

                    if (uiState.isUploadingProfileImage) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.72f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = ProfilePrimary,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Surface(
                        color = ProfilePrimary,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Change profile image",
                            tint = Color.White,
                            modifier = Modifier.padding(7.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = displayName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ProfileTitle)
                Text(text = primaryContact, color = ProfileBody, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (uiState.isUploadingProfileImage) "Uploading photo..." else "Tap photo to update",
                    color = ProfilePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = viewModel.memberSinceLabel(),
                    color = ProfileBody,
                    fontSize = 12.sp
                )
            }

            uiState.errorMessage?.let { message ->
                AppStatusBanner(
                    title = "Profile unavailable",
                    message = message,
                    tone = AppStatusTone.Error,
                    onDismiss = viewModel::dismissError,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                ProfileMenuItem(
                    icon = Icons.Outlined.LocationOn,
                    title = "Shipping Addresses",
                    subtitle = if (uiState.addresses.isEmpty()) "No saved addresses yet" else "${uiState.addresses.size} saved address(es)",
                    onClick = onShippingAddressClick
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.Inventory2,
                    title = "Subscription Details",
                    subtitle = if (activeSubscriptionCount > 0) "Manage your active plan" else "Explore plans to get started",
                    onClick = onSubscriptionDetailsClick
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
                        imageVector = Icons.Outlined.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text("Log Out", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

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
                        imageVector = Icons.Outlined.ExitToApp,
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
                    androidx.compose.material3.OutlinedButton(
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
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ProfileStroke),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(ProfilePrimary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ProfilePrimary)
            }
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(title, fontWeight = FontWeight.Bold, color = ProfileTitle, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(3.dp))
                Text(subtitle, color = ProfileBody, fontSize = 12.sp)
            }
            Text("View", color = ProfilePrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}
