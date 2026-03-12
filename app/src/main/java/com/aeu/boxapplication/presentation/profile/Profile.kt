package com.aeu.boxapplication.presentation.subscriber

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
private val ProfileBackground = Color(0xFFF3F5F9)
private val ProfileCardTop = Color(0xFFEAF4FF)
private val ProfileCardTopAccent = Color(0xFFD5E9FF)

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
    val subscriptionSummary = if (activeSubscriptionCount > 0) {
        "$activeSubscriptionCount active plan" + if (activeSubscriptionCount > 1) "s" else ""
    } else {
        "No active plan yet"
    }
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
        containerColor = ProfileBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfileBackground)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
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
                }

                item {
                    ProfileHeroCard(
                        displayName = displayName,
                        primaryContact = primaryContact,
                        memberSinceLabel = viewModel.memberSinceLabel(),
                        subscriptionSummary = subscriptionSummary,
                        initials = initials,
                        profileImageUrl = profileImageUrl,
                        isUploadingProfileImage = uiState.isUploadingProfileImage,
                        onPhotoClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                }

                if (uiState.errorMessage != null) {
                    item {
                        AppStatusBanner(
                            title = "Profile unavailable",
                            message = uiState.errorMessage,
                            tone = AppStatusTone.Error,
                            onDismiss = viewModel::dismissError
                        )
                    }
                }

                item {
                    Column {
                        Text(
                            text = "Manage",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProfileTitle
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ProfileMenuItem(
                            icon = Icons.Outlined.LocationOn,
                            title = "Shipping Addresses",
                            subtitle = if (uiState.addresses.isEmpty()) {
                                "No saved addresses yet"
                            } else {
                                "${uiState.addresses.size} saved address(es)"
                            },
                            actionLabel = if (uiState.addresses.isEmpty()) "Add" else "View",
                            onClick = onShippingAddressClick
                        )
                        ProfileMenuItem(
                            icon = Icons.Outlined.Inventory2,
                            title = "Subscription Details",
                            subtitle = if (activeSubscriptionCount > 0) {
                                "Manage your active plan"
                            } else {
                                "Explore plans to get started"
                            },
                            actionLabel = if (activeSubscriptionCount > 0) "Manage" else "Browse",
                            onClick = onSubscriptionDetailsClick
                        )
                    }
                }

                item {
                    LogoutActionButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showLogoutDialog = true }
                    )
                }
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
private fun ProfileHeroCard(
    displayName: String,
    primaryContact: String,
    memberSinceLabel: String,
    subscriptionSummary: String,
    initials: String,
    profileImageUrl: String?,
    isUploadingProfileImage: Boolean,
    onPhotoClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ProfileStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ProfileCardTop, ProfileCardTopAccent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .border(2.dp, ProfilePrimary, CircleShape)
                        .clickable(onClick = onPhotoClick),
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
                            fontSize = 30.sp
                        )
                    }

                    if (isUploadingProfileImage) {
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
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = displayName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileTitle
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = primaryContact,
                    color = ProfileBody,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onPhotoClick,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, ProfilePrimary.copy(alpha = 0.24f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = ProfilePrimary
                    )
                ) {
                    Text(
                        text = if (isUploadingProfileImage) "Uploading photo..." else "Change profile photo",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileMetaChip(label = memberSinceLabel)
                    ProfileMetaChip(label = subscriptionSummary)
                }
            }
        }
    }
}

@Composable
private fun ProfileMetaChip(label: String) {
    Surface(
        color = ProfileBackground,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, ProfileStroke)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = ProfileTitle
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
private fun LogoutActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFFFD6DE)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = ProfileDanger
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ExitToApp,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Log out",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ProfileStroke),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(ProfilePrimary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ProfilePrimary, modifier = Modifier.size(22.dp))
            }
            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f)
            ) {
                Text(title, fontWeight = FontWeight.Bold, color = ProfileTitle, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(3.dp))
                Text(subtitle, color = ProfileBody, fontSize = 12.sp, lineHeight = 18.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = ProfilePrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = actionLabel,
                        color = ProfilePrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = ProfileBody,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
