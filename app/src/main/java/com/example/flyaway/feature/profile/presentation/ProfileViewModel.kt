package com.example.flyaway.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // private val profileRepository: ProfileRepository // Comentado hasta implementar
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    init {
        // En una implementación real, cargaríamos los datos del usuario
        // viewModelScope.launch {
        //    val userData = profileRepository.getUserProfile()
        //    updateState(userData)
        // }
        
        // Por ahora, usamos datos ficticios
        _state.update { 
            it.copy(
                name = "Usuario Demo",
                email = "usuario@flyaway.com",
                phone = "+34 123 456 789"
            ) 
        }
    }
    
    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnNameChange -> {
                _state.update { it.copy(name = event.name) }
            }
            is ProfileEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email) }
            }
            is ProfileEvent.OnPhoneChange -> {
                _state.update { it.copy(phone = event.phone) }
            }
            is ProfileEvent.OnChangePhoto -> {
                // En una implementación real, mostraríamos el selector de imágenes
            }
            is ProfileEvent.OnEditProfile -> {
                _state.update { it.copy(isEditing = true) }
            }
            is ProfileEvent.OnSaveProfile -> {
                _state.update { it.copy(isEditing = false) }
                saveProfile()
            }
            is ProfileEvent.OnConfirmSave -> {
                // No es necesario hacer nada aquí, simplemente cerrar el diálogo
            }
        }
    }
    
    private fun saveProfile() {
        viewModelScope.launch {
            // En una implementación real, guardaríamos los datos actualizados
            // profileRepository.saveUserProfile(state.value.toUserData())
        }
    }
}

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUri: String? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ProfileEvent {
    data class OnNameChange(val name: String) : ProfileEvent()
    data class OnEmailChange(val email: String) : ProfileEvent()
    data class OnPhoneChange(val phone: String) : ProfileEvent()
    object OnChangePhoto : ProfileEvent()
    object OnEditProfile : ProfileEvent()
    object OnSaveProfile : ProfileEvent()
    object OnConfirmSave : ProfileEvent()
} 