package com.example.flyaway.core.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.flyaway.core.data.model.Trip
import com.example.flyaway.core.data.model.Day
import com.example.flyaway.core.data.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar los datos de viajes.
 * Esta implementación usa memoria interna para almacenar los datos temporalmente.
 * En una implementación real, esto se conectaría a una base de datos o API.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class TripRepository @Inject constructor() {
    
    // Almacenamiento en memoria para los viajes
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    
    // Obtener todos los viajes como un flujo
    fun getAllTrips(): Flow<List<Trip>> {
        return _trips.asStateFlow()
    }
    
    // Obtener un viaje específico por ID
    fun getTripById(tripId: String): Flow<Trip?> {
        return _trips.map { trips -> 
            trips.find { it.id == tripId }
        }
    }
    
    // Guardar un nuevo viaje
    suspend fun saveTrip(trip: Trip): Trip {
        val currentTrips = _trips.value.toMutableList()
        
        // Comprobar si es una actualización o un nuevo viaje
        val index = currentTrips.indexOfFirst { it.id == trip.id }
        
        if (index >= 0) {
            // Actualizar viaje existente
            currentTrips[index] = trip
        } else {
            // Añadir nuevo viaje
            currentTrips.add(trip)
        }
        
        _trips.value = currentTrips
        return trip
    }
    
    // Eliminar un viaje
    suspend fun deleteTrip(tripId: String) {
        val currentTrips = _trips.value.toMutableList()
        currentTrips.removeIf { it.id == tripId }
        _trips.value = currentTrips
    }
    
    // Crear días iniciales para un viaje basado en su duración
    suspend fun createInitialDaysForTrip(trip: Trip): Trip {
        val daysBetween = ChronoUnit.DAYS.between(trip.startDate, trip.endDate).toInt() + 1
        val days = (0 until daysBetween).map { dayOffset ->
            val date = trip.startDate.plusDays(dayOffset.toLong())
            Day(
                tripId = trip.id,
                date = date,
                dayNumber = dayOffset + 1
            )
        }
        
        // Crear un nuevo viaje con los días
        val updatedTrip = trip.copy(days = days)
        
        // Guardar el viaje actualizado
        return saveTrip(updatedTrip)
    }
    
    // Añadir o actualizar un día
    suspend fun saveDay(day: Day): Trip? {
        val trip = _trips.value.find { it.id == day.tripId } ?: return null
        
        val currentDays = trip.days.toMutableList()
        val index = currentDays.indexOfFirst { it.id == day.id }
        
        if (index >= 0) {
            // Actualizar día existente
            currentDays[index] = day
        } else {
            // Añadir nuevo día
            currentDays.add(day)
        }
        
        // Ordenar los días por número de día
        val sortedDays = currentDays.sortedBy { it.dayNumber }
        
        // Actualizar el viaje con los nuevos días
        val updatedTrip = trip.copy(days = sortedDays)
        saveTrip(updatedTrip)
        
        return updatedTrip
    }
    
    // Añadir o actualizar una actividad a un día
    suspend fun saveActivity(activity: Activity): Day? {
        // Encontrar el viaje que contiene el día
        val trip = _trips.value.find { trip ->
            trip.days.any { it.id == activity.dayId }
        } ?: return null
        
        // Encontrar el día al que pertenece la actividad
        val day = trip.days.find { it.id == activity.dayId } ?: return null
        
        // Actualizar las actividades del día
        val currentActivities = day.activities.toMutableList()
        val index = currentActivities.indexOfFirst { it.id == activity.id }
        
        if (index >= 0) {
            // Actualizar actividad existente
            currentActivities[index] = activity
        } else {
            // Añadir nueva actividad
            currentActivities.add(activity)
        }
        
        // Ordenar las actividades por hora de inicio
        val sortedActivities = currentActivities.sortedBy { it.startTime }
        
        // Actualizar el día con las nuevas actividades
        val updatedDay = day.copy(activities = sortedActivities)
        
        // Guardar el día actualizado
        saveDay(updatedDay)
        
        return updatedDay
    }
    
    // Eliminar un día
    suspend fun deleteDay(tripId: String, dayId: String): Trip? {
        val trip = _trips.value.find { it.id == tripId } ?: return null
        
        val updatedDays = trip.days.toMutableList()
        updatedDays.removeIf { it.id == dayId }
        
        // Actualizar la numeración de los días
        val renumberedDays = updatedDays.sortedBy { it.date }
            .mapIndexed { index, day -> day.copy(dayNumber = index + 1) }
        
        // Actualizar el viaje con los días actualizados
        val updatedTrip = trip.copy(days = renumberedDays)
        saveTrip(updatedTrip)
        
        return updatedTrip
    }
    
    // Eliminar una actividad
    suspend fun deleteActivity(dayId: String, activityId: String): Day? {
        // Encontrar el viaje que contiene el día
        val trip = _trips.value.find { trip ->
            trip.days.any { it.id == dayId }
        } ?: return null
        
        // Encontrar el día al que pertenece la actividad
        val day = trip.days.find { it.id == dayId } ?: return null
        
        // Eliminar la actividad
        val updatedActivities = day.activities.toMutableList()
        updatedActivities.removeIf { it.id == activityId }
        
        // Actualizar el día con las actividades actualizadas
        val updatedDay = day.copy(activities = updatedActivities)
        
        // Guardar el día actualizado
        saveDay(updatedDay)
        
        return updatedDay
    }
} 