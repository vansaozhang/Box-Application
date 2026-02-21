package com.aeu.boxapplication.presentation.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.aeu.boxapplication.core.utils.ValidationUtils
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import com.aeu.boxapplication.ui.components.AppTextField
import com.aeu.boxapplication.ui.components.SocialCircleButton

@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    navController: NavHostController,
    onRegisterSuccess: () -> Unit
) {
    val (username, setUsername) = remember { mutableStateOf("") }
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (showErrors, setShowErrors) = remember { mutableStateOf(false) }

    val isUsernameValid = username.isNotBlank()
    val isEmailValid = ValidationUtils.isValidEmail(email)
    val isPasswordValid = ValidationUtils.isValidPassword(password)
    val isFormValid = isUsernameValid && isEmailValid && isPasswordValid

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF6FAFF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Create ")
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF1E88E5),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Account")
                        }
                    },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F3A4A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Fill your information below or register with\nyour social account.",
                    fontSize = 14.sp,
                    color = Color(0xFF7B8794),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppTextField(
                    value = username,
                    onValueChange = setUsername,
                    label = "Username",
                    leadingIcon = Icons.Outlined.Person,
                    placeholder = "Jane Doe",
                    isError = showErrors && !isUsernameValid,
                    errorMessage = if (showErrors && !isUsernameValid) "Username is required" else null
                )

                Spacer(modifier = Modifier.height(14.dp))

                AppTextField(
                    value = email,
                    onValueChange = setEmail,
                    label = "Email Address",
                    leadingIcon = Icons.Outlined.Email,
                    placeholder = "hello@email.com",
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    isError = showErrors && !isEmailValid,
                    errorMessage = if (showErrors && !isEmailValid) "Enter a valid email" else null
                )

                Spacer(modifier = Modifier.height(14.dp))

                AppTextField(
                    value = password,
                    onValueChange = setPassword,
                    label = "Password",
                    leadingIcon = Icons.Outlined.Lock,
                    placeholder = "••••••••",
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    isError = showErrors && !isPasswordValid,
                    errorMessage = if (showErrors && !isPasswordValid) {
                        "At least 8 chars with upper, lower, and number"
                    } else {
                        null
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppPrimaryButton(
                    text = "Continue",
                    onClick = {
                        if (isFormValid) {
                            onContinue()
                        } else {
                            setShowErrors(true)
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(color = Color(0xFFD7E0EA), modifier = Modifier.weight(1f))
                    Text(
                        text = "  Or signup with  ",
                        fontSize = 12.sp,
                        color = Color(0xFF8A97A6)
                    )
                    Divider(color = Color(0xFFD7E0EA), modifier = Modifier.weight(1f))
                }


                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 13.sp,
                        color = Color(0xFF7B8794)
                    )
                    Text(
                        text = "Login",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300),
                        modifier = Modifier.clickable(
                            onClick = onLoginClick,
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

    }
}
