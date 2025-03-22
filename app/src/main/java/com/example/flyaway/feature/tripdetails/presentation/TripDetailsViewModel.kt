package com.example.flyaway.feature.tripdetails.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.core.data.model.Activity
import com.example.flyaway.core.data.model.Day
import com.example.flyaway.core.data.model.Trip
import com.example.flyaway.core.data.repository.TripRepository
import com.example.flyaway.core.navigation.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(TripDetailsState())
    val state: StateFlow<TripDetailsState> = _state.asStateFlow()
    
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    private val tripId: String = savedStateHandle[AppDestinations.TripDetails.TRIP_ID_PARAM] 
        ?: throw IllegalArgumentException("Trip ID es necesario")
    
    init {
        loadTripDetails()
    }
    
    fun onEvent(event: TripDetailsEvent) {
        viewModelScope.launch {
            when (event) {
                is TripDetailsEvent.LoadTrip -> {
                    loadTripById(event.tripId)
                }
                is TripDetailsEvent.OnAddDayClick -> {
                    _state.update { it.copy(showAddDayDialog = true) }
                }
                is TripDetailsEvent.OnDayDialogDismiss -> {
                    _state.update { it.copy(showAddDayDialog = false) }
                }
                is TripDetailsEvent.OnDayDateChange -> {
                    _state.update { it.copy(newDayDate = event.date) }
                }
                is TripDetailsEvent.OnSaveDay -> {
                    if (state.value.dayToEdit != null) {
                        editDay()
                    } else {
                        addDay()
                    }
                }
                is TripDetailsEvent.OnDayClick -> {
                    _state.update { it.copy(selectedDay = event.day) }
                }
                is TripDetailsEvent.OnAddActivityClick -> {
                    _state.update { 
                        it.copy(
                            showAddActivityDialog = true,
                            selectedDayId = event.dayId,
                            newActivityName = "",
                            newActivityDescription = "",
                            newActivityLocation = "",
                            newActivityStartTime = LocalTime.of(8, 0),
                            newActivityEndTime = LocalTime.of(9, 0)
                        ) 
                    }
                }
                is TripDetailsEvent.OnActivityDialogDismiss -> {
                    resetActivityDialogState()
                }
                is TripDetailsEvent.OnActivityNameChange -> {
                    _state.update { it.copy(newActivityName = event.name) }
                }
                is TripDetailsEvent.OnActivityDescriptionChange -> {
                    _state.update { it.copy(newActivityDescription = event.description) }
                }
                is TripDetailsEvent.OnActivityLocationChange -> {
                    _state.update { it.copy(newActivityLocation = event.location) }
                }
                is TripDetailsEvent.OnActivityStartTimeChange -> {
                    _state.update { it.copy(newActivityStartTime = event.time) }
                }
                is TripDetailsEvent.OnActivityEndTimeChange -> {
                    _state.update { it.copy(newActivityEndTime = event.time) }
                }
                is TripDetailsEvent.OnSaveActivity -> {
                    if (state.value.activityToEdit != null) {
                        editActivity()
                    } else {
                        addActivity()
                    }
                }
                is TripDetailsEvent.OnEditTripClick -> {
                    state.value.trip?.let { trip ->
                        _state.update { it.copy(
                            showEditTripDialog = true,
                            editedTripName = trip.name,
                            editedTripDestination = trip.destination,
                            editedTripStartDate = trip.startDate,
                            editedTripEndDate = trip.endDate
                        ) }
                    }
                }
                is TripDetailsEvent.OnDeleteTripClick -> {
                    _state.update { it.copy(showDeleteTripDialog = true) }
                }
                is TripDetailsEvent.OnDeleteTripConfirm -> {
                    deleteTrip()
                }
                is TripDetailsEvent.OnDeleteTripDismiss -> {
                    _state.update { it.copy(showDeleteTripDialog = false) }
                }
                is TripDetailsEvent.OnEditDayClick -> {
                    _state.update { 
                        it.copy(
                            dayToEdit = event.day,
                            showEditDayDialog = true,
                            newDayDate = event.day.date
                        ) 
                    }
                }
                is TripDetailsEvent.OnDeleteDayClick -> {
                    _state.update { 
                        it.copy(
                            dayToEdit = event.day,
                            showDeleteDayDialog = true
                        ) 
                    }
                }
                is TripDetailsEvent.OnDeleteDayConfirm -> {
                    deleteDay()
                }
                is TripDetailsEvent.OnDeleteDayDismiss -> {
                    _state.update { it.copy(showDeleteDayDialog = false) }
                }
                is TripDetailsEvent.OnEditActivityClick -> {
                    _state.update { 
                        it.copy(
                            activityToEdit = event.activity,
                            selectedDay = state.value.trip?.days?.find { day -> 
                                day.id == event.activity.dayId 
                            },
                            showEditActivityDialog = true,
                            newActivityName = event.activity.name,
                            newActivityDescription = event.activity.description ?: "",
                            newActivityLocation = event.activity.location ?: "",
                            newActivityStartTime = event.activity.startTime ?: LocalTime.of(8, 0),
                            newActivityEndTime = event.activity.endTime ?: LocalTime.of(9, 0)
                        ) 
                    }
                }
                is TripDetailsEvent.OnDeleteActivityClick -> {
                    _state.update { 
                        it.copy(
                            activityToEdit = event.activity,
                            showDeleteActivityDialog = true
                        ) 
                    }
                }
                is TripDetailsEvent.OnDeleteActivityConfirm -> {
                    deleteActivity()
                }
                is TripDetailsEvent.OnDeleteActivityDismiss -> {
                    _state.update { it.copy(showDeleteActivityDialog = false) }
                }
                is TripDetailsEvent.OnTripNameChange -> {
                    _state.update { it.copy(editedTripName = event.name) }
                }
                is TripDetailsEvent.OnTripDestinationChange -> {
                    _state.update { it.copy(editedTripDestination = event.destination) }
                }
                is TripDetailsEvent.OnTripStartDateChange -> {
                    _state.update { it.copy(editedTripStartDate = event.date) }
                }
                is TripDetailsEvent.OnTripEndDateChange -> {
                    _state.update { it.copy(editedTripEndDate = event.date) }
                }
                is TripDetailsEvent.OnEditTripConfirm -> {
                    editTrip()
                }
                is TripDetailsEvent.OnEditTripDismiss -> {
                    _state.update { it.copy(showEditTripDialog = false) }
                }
            }
        }
    }
    
    private fun loadTripDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.getTripById(tripId).collect { trip ->
                    trip?.let {
                        _state.update { state ->
                            state.copy(
                                trip = trip,
                                isLoading = false
                            )
                        }
                    } ?: run {
                        _state.update { it.copy(
                            error = "No se encontró el viaje",
                            isLoading = false
                        ) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Error desconocido",
                    isLoading = false
                ) }
            }
        }
    }
    
    private fun loadTripById(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.getTripById(id).collect { trip ->
                    trip?.let {
                        _state.update { state ->
                            state.copy(
                                trip = trip,
                                isLoading = false
                            )
                        }
                    } ?: run {
                        _state.update { it.copy(
                            error = "No se encontró el viaje",
                            isLoading = false
                        ) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Error desconocido",
                    isLoading = false
                ) }
            }
        }
    }
    
    private fun addDay() {
        viewModelScope.launch {
            val trip = _state.value.trip ?: return@launch
            val newDayDate = _state.value.newDayDate
            
            // Validar que la fecha esté dentro del rango del viaje
            if (newDayDate.isBefore(trip.startDate) || newDayDate.isAfter(trip.endDate)) {
                _state.update { it.copy(
                    error = "La fecha debe estar dentro del rango del viaje",
                    showAddDayDialog = false
                ) }
                return@launch
            }
            
            // Validar que no exista ya un día con esa fecha
            if (trip.days.any { it.date == newDayDate }) {
                _state.update { it.copy(
                    error = "Ya existe un día para esta fecha",
                    showAddDayDialog = false
                ) }
                return@launch
            }
            
            try {
                // Calcular el número de día dentro del viaje
                val dayNumber = trip.days.size + 1
                
                // Crear el nuevo día
                val newDay = Day(
                    tripId = trip.id,
                    date = newDayDate,
                    dayNumber = dayNumber
                )
                
                // Guardar el día
                tripRepository.saveDay(newDay)
                
                // Actualizar el estado
                _state.update { it.copy(
                    showAddDayDialog = false
                ) }
                
                // Recargar los detalles del viaje
                loadTripDetails()
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Error al añadir el día",
                    showAddDayDialog = false
                ) }
            }
        }
    }
    
    private fun addActivity() {
        viewModelScope.launch {
            val dayId = _state.value.selectedDayId ?: return@launch
            val activityName = _state.value.newActivityName
            val activityDescription = _state.value.newActivityDescription
            val activityLocation = _state.value.newActivityLocation
            val startTime = _state.value.newActivityStartTime
            val endTime = _state.value.newActivityEndTime
            
            // Validaciones
            if (activityName.isBlank()) {
                _state.update { it.copy(error = "El nombre de la actividad no puede estar vacío") }
                return@launch
            }
            
            if (endTime.isBefore(startTime)) {
                _state.update { it.copy(error = "La hora de fin no puede ser anterior a la hora de inicio") }
                return@launch
            }
            
            try {
                // Obtener el día y sus actividades
                val day = state.value.trip?.days?.find { it.id == dayId }
                if (day == null) {
                    _state.update { it.copy(error = "No se encontró el día seleccionado") }
                    return@launch
                }
                
                // Verificar si hay solapamiento con otras actividades
                val hasOverlap = day.activities.any { activity ->
                    activity.startTime?.let { activityStart ->
                        activity.endTime?.let { activityEnd ->
                            // Verificar si hay solapamiento
                            // Nota: Permitir que una actividad comience exactamente cuando otra termina
                            (startTime.isBefore(activityEnd)) && 
                            (endTime.isAfter(activityStart))
                        } ?: false
                    } ?: false
                }
                
                if (hasOverlap) {
                    // Mostrar error pero no cerrar el diálogo
                    _state.update { it.copy(error = "La actividad se solapa con otra actividad existente. Por favor, elige otro horario.") }
                    return@launch
                }
                
                // Crear la nueva actividad
                val newActivity = Activity(
                    dayId = dayId,
                    name = activityName,
                    description = activityDescription,
                    startTime = startTime,
                    endTime = endTime,
                    location = activityLocation
                )
                
                // Guardar la actividad
                tripRepository.saveActivity(newActivity)
                
                // Actualizar el estado
                _state.update { it.copy(
                    showAddActivityDialog = false,
                    selectedDayId = null,
                    error = null // Limpiar cualquier error previo
                ) }
                
                // Recargar los detalles del viaje
                loadTripDetails()
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Error al añadir la actividad"
                ) }
            }
        }
    }
    
    private fun deleteTrip() {
        viewModelScope.launch {
            state.value.trip?.id?.let { tripId ->
                try {
                    tripRepository.deleteTrip(tripId)
                    // Actualizar el estado para indicar que el viaje se ha eliminado
                    _state.update { it.copy(
                        showDeleteTripDialog = false,
                        tripDeleted = true
                    ) }
                } catch (e: Exception) {
                    _state.update { it.copy(error = e.message ?: "Error al eliminar el viaje") }
                }
            }
        }
    }
    
    private fun deleteDay() {
        viewModelScope.launch {
            val dayToDelete = state.value.dayToEdit
            val tripId = state.value.trip?.id
            
            if (dayToDelete != null && tripId != null) {
                tripRepository.deleteDay(tripId, dayToDelete.id)
                _state.update { it.copy(
                    showDeleteDayDialog = false,
                    dayToEdit = null
                ) }
                
                // Recargar el viaje
                loadTripById(tripId)
            }
        }
    }
    
    private fun deleteActivity() {
        viewModelScope.launch {
            val activityToDelete = state.value.activityToEdit
            
            if (activityToDelete != null) {
                tripRepository.deleteActivity(activityToDelete.dayId, activityToDelete.id)
                _state.update { it.copy(
                    showDeleteActivityDialog = false,
                    activityToEdit = null
                ) }
                
                // Recargar el viaje
                state.value.trip?.let { loadTripById(it.id) }
            }
        }
    }
    
    private fun editDay() {
        viewModelScope.launch {
            val dayToEdit = state.value.dayToEdit
            
            if (dayToEdit != null && state.value.trip != null) {
                val updatedDay = dayToEdit.copy(
                    date = state.value.newDayDate
                )
                
                tripRepository.saveDay(updatedDay)
                _state.update { it.copy(
                    showEditDayDialog = false,
                    dayToEdit = null
                ) }
                
                // Recargar el viaje
                state.value.trip?.let { loadTripById(it.id) }
            }
        }
    }
    
    private fun editActivity() {
        viewModelScope.launch {
            val activityToEdit = state.value.activityToEdit
            val selectedDay = state.value.selectedDay
            
            if (activityToEdit != null && selectedDay != null) {
                val newName = state.value.newActivityName
                val newDescription = state.value.newActivityDescription
                val newLocation = state.value.newActivityLocation
                val newStartTime = state.value.newActivityStartTime
                val newEndTime = state.value.newActivityEndTime
                
                // Validar nombre
                if (newName.isBlank()) {
                    _state.update { it.copy(error = "El nombre de la actividad no puede estar vacío") }
                    return@launch
                }
                
                // Validar horarios
                if (newEndTime.isBefore(newStartTime)) {
                    _state.update { it.copy(error = "La hora de fin no puede ser anterior a la hora de inicio") }
                    return@launch
                }
                
                // Validar si hay solapamiento con otras actividades
                val hasOverlap = selectedDay.activities.any { activity ->
                    // No comparar con la actividad que estamos editando
                    if (activity.id == activityToEdit.id) {
                        return@any false
                    }
                    
                    activity.startTime?.let { activityStart ->
                        activity.endTime?.let { activityEnd ->
                            // Verificar si hay solapamiento
                            // Nota: Permitir que una actividad comience exactamente cuando otra termina
                            (newStartTime.isBefore(activityEnd)) && 
                            (newEndTime.isAfter(activityStart))
                        } ?: false
                    } ?: false
                }
                
                if (hasOverlap) {
                    // Mostrar error pero mantener abierto el diálogo para edición
                    _state.update { it.copy(error = "La actividad se solapa con otra actividad existente. Por favor, elige otro horario.") }
                    return@launch
                }
                
                val updatedActivity = activityToEdit.copy(
                    name = newName,
                    description = newDescription,
                    location = newLocation,
                    startTime = newStartTime,
                    endTime = newEndTime
                )
                
                tripRepository.saveActivity(updatedActivity)
                _state.update { it.copy(
                    showEditActivityDialog = false,
                    activityToEdit = null,
                    error = null // Limpiar cualquier error previo
                ) }
                
                // Recargar el viaje
                state.value.trip?.let { loadTripById(it.id) }
            }
        }
    }
    
    private fun editTrip() {
        viewModelScope.launch {
            try {
                state.value.trip?.let { existingTrip ->
                    // Guardar la fecha de inicio y fin anteriores para comparar
                    val oldStartDate = existingTrip.startDate
                    val oldEndDate = existingTrip.endDate
                    
                    // Crear viaje actualizado
                    val updatedTrip = existingTrip.copy(
                        name = state.value.editedTripName,
                        destination = state.value.editedTripDestination,
                        startDate = state.value.editedTripStartDate,
                        endDate = state.value.editedTripEndDate
                    )
                    
                    // Si cambió la duración, ajustar los días
                    if (oldStartDate != updatedTrip.startDate || oldEndDate != updatedTrip.endDate) {
                        // Calcular el nuevo rango de fechas válidas
                        val newDateRange = updatedTrip.startDate.datesUntil(updatedTrip.endDate.plusDays(1))
                            .collect(java.util.stream.Collectors.toList())
                        
                        // Obtener la duración del viaje en días
                        val newDuration = ChronoUnit.DAYS.between(updatedTrip.startDate, updatedTrip.endDate) + 1
                        
                        // Crear exactamente tantos días como la duración del viaje
                        val finalDays = (0 until newDuration.toInt()).map { index ->
                            // Si hay un día existente con este índice, reusarlo con nueva fecha
                            // Si no, crear un nuevo día
                            if (index < updatedTrip.days.size) {
                                val existingDay = updatedTrip.days[index]
                                existingDay.copy(
                                    date = updatedTrip.startDate.plusDays(index.toLong()),
                                    dayNumber = index + 1 // Renumerar del 1 en adelante
                                )
                            } else {
                                // Crear un nuevo día
                                Day(
                                    tripId = updatedTrip.id,
                                    date = updatedTrip.startDate.plusDays(index.toLong()),
                                    dayNumber = index + 1
                                )
                            }
                        }
                        
                        // Crear una copia del viaje con los días ajustados
                        val adjustedTrip = updatedTrip.copy(
                            days = finalDays
                        )
                        
                        tripRepository.saveTrip(adjustedTrip)
                        _state.update { it.copy(
                            trip = adjustedTrip,
                            showEditTripDialog = false
                        ) }
                    } else {
                        // Si no cambió la duración, guardar normalmente
                        tripRepository.saveTrip(updatedTrip)
                        _state.update { it.copy(
                            trip = updatedTrip,
                            showEditTripDialog = false
                        ) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Error al editar el viaje") }
            }
        }
    }
    
    private fun resetActivityDialogState() {
        _state.update { it.copy(
            showAddActivityDialog = false,
            showEditActivityDialog = false,
            newActivityName = "",
            newActivityDescription = "",
            newActivityLocation = "",
            newActivityStartTime = LocalTime.of(8, 0),
            newActivityEndTime = LocalTime.of(9, 0),
            selectedDay = null,
            selectedDayId = null,
            activityToEdit = null,
            error = null // Limpiar cualquier mensaje de error al cerrar el diálogo
        ) }
    }
}

data class TripDetailsState(
    val trip: Trip? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Estado para el diálogo de añadir día
    val showAddDayDialog: Boolean = false,
    val newDayDate: LocalDate = LocalDate.now(),
    
    // Estado para el diálogo de añadir actividad
    val showAddActivityDialog: Boolean = false,
    val selectedDay: Day? = null,
    val selectedDayId: String? = null,
    val newActivityName: String = "",
    val newActivityDescription: String = "",
    val newActivityLocation: String = "",
    val newActivityStartTime: LocalTime = LocalTime.of(8, 0),
    val newActivityEndTime: LocalTime = LocalTime.of(9, 0),
    
    // Nuevos estados para edición y eliminación
    val showEditTripDialog: Boolean = false,
    val showDeleteTripDialog: Boolean = false,
    
    val dayToEdit: Day? = null,
    val showEditDayDialog: Boolean = false,
    val showDeleteDayDialog: Boolean = false,
    
    val activityToEdit: Activity? = null,
    val showEditActivityDialog: Boolean = false,
    val showDeleteActivityDialog: Boolean = false,
    
    val tripDeleted: Boolean = false,
    val editedTripName: String = "",
    val editedTripDestination: String = "",
    val editedTripStartDate: LocalDate = LocalDate.now(),
    val editedTripEndDate: LocalDate = LocalDate.now()
)

sealed class TripDetailsEvent {
    data class LoadTrip(val tripId: String) : TripDetailsEvent()
    data object OnAddDayClick : TripDetailsEvent()
    data object OnDayDialogDismiss : TripDetailsEvent()
    data class OnDayDateChange(val date: LocalDate) : TripDetailsEvent()
    data object OnSaveDay : TripDetailsEvent()
    data class OnDayClick(val day: Day) : TripDetailsEvent()
    data class OnAddActivityClick(val dayId: String) : TripDetailsEvent()
    data object OnActivityDialogDismiss : TripDetailsEvent()
    data class OnActivityNameChange(val name: String) : TripDetailsEvent()
    data class OnActivityDescriptionChange(val description: String) : TripDetailsEvent()
    data class OnActivityLocationChange(val location: String) : TripDetailsEvent()
    data class OnActivityStartTimeChange(val time: LocalTime) : TripDetailsEvent()
    data class OnActivityEndTimeChange(val time: LocalTime) : TripDetailsEvent()
    data object OnSaveActivity : TripDetailsEvent()
    
    data object OnEditTripClick : TripDetailsEvent()
    data object OnDeleteTripClick : TripDetailsEvent()
    data object OnDeleteTripConfirm : TripDetailsEvent()
    data object OnDeleteTripDismiss : TripDetailsEvent()
    
    data class OnEditDayClick(val day: Day) : TripDetailsEvent()
    data class OnDeleteDayClick(val day: Day) : TripDetailsEvent()
    data object OnDeleteDayConfirm : TripDetailsEvent()
    data object OnDeleteDayDismiss : TripDetailsEvent()
    
    data class OnEditActivityClick(val activity: Activity) : TripDetailsEvent()
    data class OnDeleteActivityClick(val activity: Activity) : TripDetailsEvent()
    data object OnDeleteActivityConfirm : TripDetailsEvent()
    data object OnDeleteActivityDismiss : TripDetailsEvent()
    data class OnTripNameChange(val name: String) : TripDetailsEvent()
    data class OnTripDestinationChange(val destination: String) : TripDetailsEvent()
    data class OnTripStartDateChange(val date: LocalDate) : TripDetailsEvent()
    data class OnTripEndDateChange(val date: LocalDate) : TripDetailsEvent()
    data object OnEditTripConfirm : TripDetailsEvent()
    data object OnEditTripDismiss : TripDetailsEvent()
} 