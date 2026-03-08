package com.aeu.boxapplication.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeu.boxapplication.core.utils.ValidationUtils
import com.aeu.boxapplication.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    data class RegisterState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val userName: String = "",
        val userPhone: String = "",
        val userEmail: String? = null,
        val accessToken: String = "",
        val error: String? = null
    )

    var state by mutableStateOf(RegisterState())
        private set
    // 1. Add this variable to store the name
    var nameState = mutableStateOf("")
        private set // Only the ViewModel can change it directly

    // 2. Add a function to update the name from the UI
    fun onNameChange(newName: String) {
        nameState.value = newName
    }

    fun register(name: String, phoneNumber: String, password: String) {
        if (name.isBlank() || phoneNumber.isBlank() || password.isBlank()) {
            state = state.copy(error = "All fields are required")
            return
        }

        val apiPhoneNumber = ValidationUtils.toCambodianPhoneNumber(phoneNumber)
        if (apiPhoneNumber == null) {
            state = state.copy(error = "Enter a valid Cambodia phone number")
            return
        }

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            val result = repository.registerUser(
                username = name.trim(),
                phoneNumber = apiPhoneNumber,
                password = password.trim()
            )

            result.onSuccess { authResponse ->
                state = state.copy(
                    isLoading = false,
                    isSuccess = true,
                    userName = authResponse.user?.name ?: "",
                    userPhone = authResponse.user?.phoneNumber ?: apiPhoneNumber,
                    userEmail = authResponse.user?.email,
                    accessToken = authResponse.accessToken ?: ""
                )
            }.onFailure { exception ->
                state = state.copy(
                    isLoading = false,
                    error = exception.message ?: "Registration failed"
                )
            }
        }
    }

    fun clearError() {
        state = state.copy(error = null)
    }
}
