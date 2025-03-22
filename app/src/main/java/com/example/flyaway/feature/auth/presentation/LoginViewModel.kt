package com.example.flyaway.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChange -> {
                _state.update { it.copy(
                    username = event.value,
                    usernameError = null
                ) }
            }
            is LoginEvent.OnPasswordChange -> {
                _state.update { it.copy(
                    password = event.value,
                    passwordError = null
                ) }
            }
            is LoginEvent.OnLoginClick -> login()
        }
    }

    private fun login() {
        val username = state.value.username
        val password = state.value.password
        var isValid = true

        // Validación del username
        if (username.isBlank()) {
            _state.update { it.copy(usernameError = R.string.empty_username_error) }
            isValid = false
        }

        // Validación de la contraseña
        if (password.isBlank()) {
            _state.update { it.copy(passwordError = R.string.empty_password_error) }
            isValid = false
        } else if (password.length < 6) {
            _state.update { it.copy(passwordError = R.string.password_length_error) }
            isValid = false
        }

        // Si las validaciones son correctas, intentamos login
        if (isValid) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                try {
                    // Simulamos una llamada a API 
                    kotlinx.coroutines.delay(1000)
                    
                    // Por ahora, simularemos una autenticación básica
                    if (username == "admin" && password == "123456") {
                        _state.update { it.copy(
                            isAuthenticated = true,
                            error = null
                        ) }
                    } else {
                        _state.update { it.copy(
                            error = R.string.invalid_credentials
                        ) }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(
                        error = R.string.login_error
                    ) }
                } finally {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: Int? = null
)

sealed class LoginEvent {
    data class OnUsernameChange(val value: String) : LoginEvent()
    data class OnPasswordChange(val value: String) : LoginEvent()
    data object OnLoginClick : LoginEvent()
} 