package com.aeu.boxapplication.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.text.input.*
import androidx.navigation.NavController
import com.aeu.boxapplication.core.utils.ValidationUtils
import com.aeu.boxapplication.presentation.navigation.Screen
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import com.aeu.boxapplication.ui.components.AppTextField

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel,
    onRegisterSuccess: (String, String, String?, String) -> Unit
) {
    val state = viewModel.state
    val (username, setUsername) = remember { mutableStateOf("") }
    val (phoneNumber, setPhoneNumber) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (showErrors, setShowErrors) = remember { mutableStateOf(false) }

    val isUsernameValid = username.isNotBlank()
    val isPhoneValid = ValidationUtils.isValidPhone(phoneNumber)
    val isPasswordValid = ValidationUtils.isValidPassword(password)
    val isFormValid = isUsernameValid && isPhoneValid && isPasswordValid

    AppGlobalLoadingEffect(isVisible = state.isLoading)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess(
                state.userName,
                state.userPhone,
                state.userEmail,
                state.accessToken
            )
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Create ")
                        withStyle(style = SpanStyle(color = Color(0xFF1E88E5), fontWeight = FontWeight.Bold)) {
                            append("Account")
                        }
                    },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F3A4A)
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppTextField(
                    value = username,
                    onValueChange = setUsername,
                    label = "Username",
                    leadingIcon = Icons.Outlined.Person,
                    isError = showErrors && !isUsernameValid,
                    errorMessage = if (showErrors && !isUsernameValid) "Username is required" else null
                )

                Spacer(modifier = Modifier.height(14.dp))

                AppTextField(
                    value = phoneNumber,
                    onValueChange = { setPhoneNumber(ValidationUtils.normalizeCambodianPhoneInput(it)) },
                    label = "Phone Number",
                    leadingIcon = Icons.Outlined.Call,
                    prefixText = "${ValidationUtils.CAMBODIA_PHONE_PREFIX} ",
                    placeholder = "12 345 678",
                    keyboardType = KeyboardType.Phone,
                    isError = showErrors && !isPhoneValid,
                    errorMessage = if (showErrors && !isPhoneValid) "Enter a valid Cambodia phone number" else null
                )

                Spacer(modifier = Modifier.height(14.dp))

                AppTextField(
                    value = password,
                    onValueChange = setPassword,
                    label = "Password",
                    leadingIcon = Icons.Outlined.Lock,
                    isPassword = true,
                    isError = showErrors && !isPasswordValid,
                    errorMessage = if (showErrors && !isPasswordValid) "Password too weak" else null
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                AppPrimaryButton(
                    text = if (state.isLoading) "Registering..." else "Register",
                    onClick = {
                        if (isFormValid) {
                            viewModel.register(username, phoneNumber, password)
                        } else {
                            setShowErrors(true)
                        }
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account? ", fontSize = 13.sp, color = Color(0xFF7B8794))
                    Text(
                        text = "Login",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300),
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
