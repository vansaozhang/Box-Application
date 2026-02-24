package com.aeu.boxapplication.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.aeu.boxapplication.core.utils.ValidationUtils
import com.aeu.boxapplication.presentation.LoginViewModel // Ensure this import is correct
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import com.aeu.boxapplication.ui.components.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel, // ADDED: Use your ViewModel for API logic
    onLoginSuccess: (String) -> Unit,
    onBack: () -> Unit,
    onSignupClick: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    // UI Local State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    // Validation Logic
    val isEmailValid = ValidationUtils.isValidEmail(email)
    val isPasswordValid = password.length >= 6
    val isFormValid = isEmailValid && isPasswordValid

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
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

            // --- API ERROR MESSAGE ---
            // We read this from the ViewModel now
            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            AppTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.errorMessage = null // Clear API error when typing
                },
                label = "Email Address",
                leadingIcon = Icons.Outlined.Email,
                placeholder = "example@mail.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                isError = showErrors && !isEmailValid,
                errorMessage = if (showErrors && !isEmailValid) "Please enter a valid email" else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.errorMessage = null // Clear API error when typing
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

            // --- FIXED DYNAMIC BUTTON ---

            AppPrimaryButton(
                text = if (viewModel.isLoading) "Checking Credentials..." else "Login",
                enabled = isFormValid && !viewModel.isLoading,
                onClick = {
                    if (isFormValid) {
                        // CALL REAL API
                        viewModel.performLogin(email, password) { name ->
                            onLoginSuccess(name)
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