package com.aeu.boxapplication.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.aeu.boxapplication.core.utils.ValidationUtils
import com.aeu.boxapplication.presentation.LoginViewModel
import com.aeu.boxapplication.presentation.PostLoginDestination
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import com.aeu.boxapplication.ui.components.AppNotificationPosition
import com.aeu.boxapplication.ui.components.AppStatusTone
import com.aeu.boxapplication.ui.components.AppTextField
import com.aeu.boxapplication.ui.components.LocalAppNotificationHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    onLoginSuccess: (String, PostLoginDestination) -> Unit,
    onBack: () -> Unit,
    onSignupClick: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }
    val notificationHostState = LocalAppNotificationHostState.current

    val isPhoneValid = ValidationUtils.isValidPhone(phoneNumber)
    val isPasswordValid = password.length >= 6
    val isFormValid = isPhoneValid && isPasswordValid

    AppGlobalLoadingEffect(isVisible = viewModel.isLoading)

    LaunchedEffect(viewModel.uiMessage) {
        viewModel.uiMessage?.let { message ->
            notificationHostState.show(
                title = message.title,
                message = message.message,
                tone = AppStatusTone.Error,
                position = AppNotificationPosition.Top,
                onDismiss = viewModel::clearUiMessage
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            notificationHostState.dismiss()
            viewModel.clearUiMessage()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E88E5)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        state = rememberScrollState(),
                        flingBehavior = ScrollableDefaults.flingBehavior()
                    )
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = buildAnnotatedString {
                                append("Welcome ")
                                withStyle(style = SpanStyle(color = Color(0xFF1E88E5), fontWeight = FontWeight.Bold)) {
                                    append("Back")
                                }
                            },
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2F3A4A)
                        )

                        Text(
                            text = "Login to access your account",
                            fontSize = 14.sp,
                            color = Color(0xFF7B8794),
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AppTextField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = ValidationUtils.normalizeCambodianPhoneInput(it)
                                notificationHostState.dismiss()
                                viewModel.clearUiMessage()
                            },
                            label = "Phone Number",
                            leadingIcon = Icons.Outlined.Call,
                            prefixText = "${ValidationUtils.CAMBODIA_PHONE_PREFIX} ",
                            placeholder = "12 345 678",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                            isError = showErrors && !isPhoneValid,
                            errorMessage = if (showErrors && !isPhoneValid) "Please enter a valid Cambodia phone number" else null
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AppTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                notificationHostState.dismiss()
                                viewModel.clearUiMessage()
                            },
                            label = "Password",
                            leadingIcon = Icons.Outlined.Lock,
                            placeholder = "••••••••",
                            isPassword = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            isError = showErrors && !isPasswordValid,
                            errorMessage = if (showErrors && !isPasswordValid) "Password must be at least 6 characters" else null
                        )

                        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.CenterEnd) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 13.sp,
                                color = Color(0xFFFFB300),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable(onClick = onForgotPassword)
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        AppPrimaryButton(
                            text = if (viewModel.isLoading) "Signing in..." else "Login",
                            enabled = isFormValid && !viewModel.isLoading,
                            onClick = {
                                if (isFormValid) {
                                    viewModel.performLogin(phoneNumber, password) { name, destination ->
                                        onLoginSuccess(name, destination)
                                    }
                                } else {
                                    showErrors = true
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Don't have an account? ", fontSize = 14.sp, color = Color(0xFF7B8794))
                            Text(
                                text = "Sign Up",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB300),
                                modifier = Modifier.clickable(
                                    onClick = onSignupClick,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                            )
                        }
            }
        }
    }
}
