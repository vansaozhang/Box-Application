package com.aeu.boxapplication.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.aeu.boxapplication.core.utils.ValidationUtils
import com.aeu.boxapplication.ui.components.AppPrimaryButton
import com.aeu.boxapplication.ui.components.AppTextField
import com.aeu.boxapplication.ui.components.SocialCircleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit = {},
    onLogin: () -> Unit = {},
    onSignupClick: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {}
) {
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (showErrors, setShowErrors) = remember { mutableStateOf(false) }

    val isEmailValid = ValidationUtils.isValidEmail(email)
    val isPasswordValid = password.isNotBlank()
    val isFormValid = isEmailValid && isPasswordValid

    Scaffold(
        containerColor = Color(0xFFF6FAFF),
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF6FAFF)
                )
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("Welcome ")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF1E88E5),
                            fontWeight = FontWeight.Bold
                        )
                    ) { append("Back") }
                },
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F3A4A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We missed you! Login to continue your journey\nwith us.",
                fontSize = 14.sp,
                color = Color(0xFF7B8794),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = email,
                onValueChange = setEmail,
                label = "Email Address",
                leadingIcon = Icons.Outlined.Email,
                placeholder = "JaneDoe@gmail.com",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = false
                ),
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
                errorMessage = if (showErrors && !isPasswordValid) "Password is required" else null
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Forgot Password?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFB300),
                    modifier = Modifier.clickable(
                        onClick = onForgotPassword,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            AppPrimaryButton(
                text = "Login",
                onClick = {
                    if (isFormValid) onLogin()
                    else setShowErrors(true)
                },
                enabled = isFormValid
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(color = Color(0xFFD7E0EA), modifier = Modifier.weight(1f))
                Text(
                    text = "  Or continue with  ",
                    fontSize = 12.sp,
                    color = Color(0xFF8A97A6)
                )
                Divider(color = Color(0xFFD7E0EA), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SocialCircleButton(text = "G", onClick = onGoogleClick)
                SocialCircleButton(text = "f", onClick = onFacebookClick)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Does not have an account? ",
                    fontSize = 13.sp,
                    color = Color(0xFF7B8794)
                )
                Text(
                    text = "Signup",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB300),
                    modifier = Modifier.clickable(
                        onClick = onSignupClick,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}