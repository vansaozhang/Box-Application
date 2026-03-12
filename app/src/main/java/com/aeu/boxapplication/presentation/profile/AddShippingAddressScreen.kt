package com.aeu.boxapplication.presentation.profile

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aeu.boxapplication.presentation.subscriber.SubscriberProfileViewModel
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppNotificationPosition
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import com.aeu.boxapplication.ui.components.AppStatusTone
import com.aeu.boxapplication.ui.components.AppTextField
import com.aeu.boxapplication.ui.components.LocalAppNotificationHostState
import kotlinx.coroutines.launch

private val AddAddressTitle = Color(0xFF2F3A4A)
private val AddAddressBody = Color(0xFF7B8794)
private val AddAddressTint = Color(0xFFEAF3FF)
private val AddAddressStroke = Color(0xFFE3E8EF)
private val AddAddressPrimary = Color(0xFF1E88E5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShippingAddressScreen(
    navController: NavController,
    viewModel: SubscriberProfileViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState = viewModel.uiState
    val notificationHostState = LocalAppNotificationHostState.current
    var address by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var isResolvingCurrentLocation by rememberSaveable { mutableStateOf(false) }
    var locationHelperMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.profile?.phoneNumber) {
        if (phone.isBlank()) {
            phone = uiState.profile?.phoneNumber.orEmpty()
        }
    }

    val addressError = if (showErrors && address.trim().length < 8) {
        "Enter the full street and area for delivery."
    } else {
        null
    }
    val phoneDigits = phone.filter { it.isDigit() }
    val phoneError = if (showErrors && phoneDigits.length !in 8..15) {
        "Phone number should be 8 to 15 digits."
    } else {
        null
    }
    val isFormValid = addressError == null && phoneError == null

    fun autofillCurrentLocation() {
        if (uiState.isSavingAddress || isResolvingCurrentLocation) {
            return
        }

        scope.launch {
            isResolvingCurrentLocation = true
            val result = resolveCurrentAddress(context)
            isResolvingCurrentLocation = false

            result.fold(
                onSuccess = { resolvedAddress ->
                    address = resolvedAddress
                    locationHelperMessage = null
                },
                onFailure = {
                    locationHelperMessage = "We couldn't autofill your address. Try again or enter it manually."
                }
            )
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (hasLocationPermission(context)) {
            autofillCurrentLocation()
        } else {
            locationHelperMessage = "Location access is off. Tap again to allow it, or enter the address manually."
        }
    }

    AppGlobalLoadingEffect(isVisible = uiState.isSavingAddress || isResolvingCurrentLocation)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Address",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AddAddressTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AddAddressTitle
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                AppPrimaryButton(
                    text = "Save Address",
                    onClick = saveClick@{
                        showErrors = true
                        if (!isFormValid) {
                            return@saveClick
                        }

                        viewModel.addShippingAddress(
                            address = address,
                            phone = phone,
                            onSuccess = {
                                notificationHostState.show(
                                    title = "Address saved",
                                    message = "It is now your default shipping address until you save another one.",
                                    tone = AppStatusTone.Success,
                                    label = "Saved",
                                    position = AppNotificationPosition.Top
                                )
                                navController.popBackStack()
                            },
                            onError = { saveError ->
                                notificationHostState.show(
                                    title = "Address not saved",
                                    message = saveError,
                                    tone = AppStatusTone.Error,
                                    label = "Address",
                                    position = AppNotificationPosition.Top
                                )
                            }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    isLoading = uiState.isSavingAddress
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        state = rememberScrollState(),
                        flingBehavior = ScrollableDefaults.flingBehavior()
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            Text(
                text = "Use your current location or enter the address manually. If your account already has a phone number, we use it as the delivery contact automatically.",
                color = AddAddressBody,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AddAddressTint,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AddAddressStroke)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Faster setup",
                        color = AddAddressTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap below to request permission and autofill your current address from the device location.",
                        color = AddAddressBody,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    OutlinedButton(
                        onClick = {
                            locationHelperMessage = null
                            if (hasLocationPermission(context)) {
                                autofillCurrentLocation()
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        enabled = !uiState.isSavingAddress && !isResolvingCurrentLocation,
                        border = BorderStroke(1.dp, AddAddressPrimary.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null
                        )
                        Text(
                            text = "Use Current Location",
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (!locationHelperMessage.isNullOrBlank()) {
                        Text(
                            text = locationHelperMessage.orEmpty(),
                            color = AddAddressBody,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            AppTextField(
                value = address,
                onValueChange = { address = it },
                label = "Full Address",
                leadingIcon = Icons.Outlined.LocationOn,
                placeholder = "Street, apartment, city, province",
                isError = addressError != null,
                errorMessage = addressError,
                singleLine = false,
                minLines = 3,
                maxLines = 4
            )

            AppTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                leadingIcon = Icons.Outlined.Call,
                placeholder = "Contact number for delivery",
                keyboardType = KeyboardType.Phone,
                isError = phoneError != null,
                errorMessage = phoneError
            )
        }
        }
    }
}
