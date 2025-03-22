package com.example.flyaway.domain.repository

import com.example.flyaway.domain.model.Activity
import com.example.flyaway.domain.model.Day
import com.example.flyaway.domain.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de viajes.
 * Define las operaciones disponibles sin especificar los detalles de implementación.
 */
interface ITripRepository {
    
    // Operaciones para Trip
    fun getAllTrips(): Flow<List<Trip>>
    fun getTripById(tripId: String): Flow<Trip?>
    suspend fun saveTrip(trip: Trip): Trip
    suspend fun deleteTrip(tripId: String)
    suspend fun createInitialDaysForTrip(trip: Trip): Trip
    
    // Operaciones para Day
    suspend fun saveDay(day: Day): Trip?
    suspend fun deleteDay(tripId: String, dayId: String): Trip?
    
    // Operaciones para Activity
    suspend fun saveActivity(activity: Activity): Day?
    suspend fun deleteActivity(dayId: String, activityId: String): Day?
} 