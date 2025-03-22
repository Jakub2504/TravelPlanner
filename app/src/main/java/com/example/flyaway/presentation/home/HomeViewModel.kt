package com.example.flyaway.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.usecase.trip.DeleteTripUseCase
import com.example.flyaway.domain.usecase.trip.GetTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la pantalla Home
 */
data class HomeState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val tripToDelete: Trip? = null
)

/**
 * Eventos que pueden ocurrir en la pantalla Home
 */
sealed class HomeEvent {
    object LoadTrips : HomeEvent()
    data class DeleteTrip(val trip: Trip) : HomeEvent()
    object ConfirmDelete : HomeEvent()
    object DismissDeleteDialog : HomeEvent()
}

/**
 * ViewModel para la pantalla Home.
 * Utiliza casos de uso para acceder a los datos siguiendo el patrón Clean Architecture.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTripsUseCase: GetTripsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    init {
        loadTrips()
    }
    
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadTrips -> loadTrips()
            is HomeEvent.DeleteTrip -> {
                _state.update { it.copy(
                    showDeleteConfirmation = true,
                    tripToDelete = event.trip
                )}
            }
            is HomeEvent.ConfirmDelete -> {
                deleteTrip()
            }
            is HomeEvent.DismissDeleteDialog -> {
                _state.update { it.copy(
                    showDeleteConfirmation = false,
                    tripToDelete = null
                )}
            }
        }
    }
    
    private fun loadTrips() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                getTripsUseCase().collect { trips ->
                    _state.update { it.copy(
                        trips = trips,
                        isLoading = false,
                        error = null
                    )}
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Error al cargar los viajes: ${e.message}"
                )}
            }
        }
    }
    
    private fun deleteTrip() {
        val tripToDelete = state.value.tripToDelete ?: return
        
        viewModelScope.launch {
            try {
                deleteTripUseCase(tripToDelete.id)
                _state.update { it.copy(
                    showDeleteConfirmation = false,
                    tripToDelete = null
                )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Error al eliminar el viaje: ${e.message}",
                    showDeleteConfirmation = false
                )}
            }
        }
    }
} 