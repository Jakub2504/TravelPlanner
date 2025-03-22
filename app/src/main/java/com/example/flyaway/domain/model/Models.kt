package com.example.flyaway.domain.model

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

/**
 * Modelos de dominio para la aplicación FlyAway.
 * Estas clases son independientes de la implementación de la capa de datos.
 */

// Entidad Trip (Viaje)
data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val days: List<Day> = emptyList(),
    val createdAt: LocalDate = LocalDate.now()
)

// Entidad Day (Día)
data class Day(
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val date: LocalDate,
    val dayNumber: Int,
    val activities: List<Activity> = emptyList()
)

// Entidad Activity (Actividad)
data class Activity(
    val id: String = UUID.randomUUID().toString(),
    val dayId: String,
    val name: String,
    val description: String,
    val startTime: LocalTime,
    val endTime: LocalTime? = null,
    val location: String
) 