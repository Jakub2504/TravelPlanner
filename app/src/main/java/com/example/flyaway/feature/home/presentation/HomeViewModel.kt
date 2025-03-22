package com.example.flyaway.feature.home.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.core.data.model.Trip
import com.example.flyaway.core.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la pantalla de inicio que contiene la lista de viajes
 */
data class HomeState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Eventos que pueden ocurrir en la pantalla de inicio
 */
sealed class HomeEvent {
    data object LoadTrips : HomeEvent()
    data class TripSelected(val tripId: String) : HomeEvent()
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    
    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    
    // Estado combinado de la pantalla
    val state: StateFlow<HomeState> = combine(
        tripRepository.getAllTrips(),
        _isLoading,
        _error
    ) { trips, isLoading, error ->
        HomeState(
            trips = trips,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState(isLoading = true)
    )
    
    init {
        loadTrips()
    }
    
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadTrips -> loadTrips()
            is HomeEvent.TripSelected -> {
                // Este evento sería manejado por la capa de UI para navegar
                // No necesitamos hacer nada aquí
            }
        }
    }
    
    private fun loadTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // En una implementación real, aquí podríamos forzar una recarga
                // desde el repositorio si es necesario
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
} 