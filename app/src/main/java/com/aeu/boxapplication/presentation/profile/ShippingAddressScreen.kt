package com.aeu.boxapplication.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.presentation.subscriber.SubscriberProfileViewModel
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import com.aeu.boxapplication.ui.components.AppStatusBanner
import com.aeu.boxapplication.ui.components.AppStatusTone

private val AddressPrimary = Color(0xFF1E88E5)
private val AddressTitle = Color(0xFF2F3A4A)
private val AddressBody = Color(0xFF7B8794)
private val AddressStroke = Color(0xFFE3E8EF)
private val AddressTint = Color(0xFFEAF3FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressScreen(
    navController: NavController,
    viewModel: SubscriberProfileViewModel
) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    AppGlobalLoadingEffect(isVisible = uiState.isLoading && uiState.addresses.isEmpty())

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Shipping Addresses",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AddressTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AddressTitle
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AddShipAddress.route) }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add address",
                            tint = AddressPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(
                    state = rememberScrollState(),
                    flingBehavior = ScrollableDefaults.flingBehavior()
                )
                .padding(paddingValues)
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Saved Addresses",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AddressBody
                    )
                    Text(
                        text = "You can keep multiple addresses here. The newest saved address is treated as your default shipping address.",
                        fontSize = 12.sp,
                        color = AddressBody
                    )
            }

            if (uiState.isSavingAddress) {
                AppStatusBanner(
                        title = "Saving address",
                        message = "Your address is being synced to your account.",
                        tone = AppStatusTone.Info
                    )
            }

            uiState.errorMessage?.let { message ->
                AppStatusBanner(
                        title = "Addresses unavailable",
                        message = message,
                        tone = AppStatusTone.Error,
                        onDismiss = viewModel::dismissError
                    )
            }

            if (uiState.addresses.isEmpty() && !uiState.isLoading && uiState.errorMessage == null) {
                EmptyAddressCard(
                        onAddAddressClick = {
                            navController.navigate(Screen.AddShipAddress.route)
                        }
                    )
            } else {
                uiState.addresses.forEach { address ->
                    AddressCard(
                        id = address.id,
                        label = address.label,
                        address = address.address,
                        phoneNumber = address.phone,
                        isDefault = address.isPrimary,
                        onEditClick = {
                            navController.navigate(Screen.EditShipAddress.createRoute(address.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressCard(
    id: String,
    label: String,
    address: String,
    phoneNumber: String?,
    isDefault: Boolean,
    onEditClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(
            if (isDefault) 1.5.dp else 1.dp,
            if (isDefault) AddressPrimary else AddressStroke
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(AddressTint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AddressPrimary
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AddressTitle)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isDefault) {
                        Surface(
                            color = AddressTint,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "RECENT",
                                color = AddressPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    TextButton(
                        onClick = { onEditClick(id) },
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Edit",
                            color = AddressPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = address,
                color = AddressBody,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            phoneNumber?.takeIf { it.isNotBlank() }?.let { phone ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = phone, color = AddressBody, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun EmptyAddressCard(
    onAddAddressClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AddressStroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(AddressTint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = AddressPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("No addresses saved", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AddressTitle)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Add your first shipping address so it can be used for checkout and deliveries.",
                fontSize = 13.sp,
                color = AddressBody
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppPrimaryButton(
                text = "Add Address",
                onClick = onAddAddressClick
            )
        }
    }
}
