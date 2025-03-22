package com.example.flyaway.core.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

// Modelo para viajes
@RequiresApi(Build.VERSION_CODES.O)
data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val days: List<Day> = emptyList(),
    val createdAt: LocalDate = LocalDate.now()
)

// Modelo para días en el itinerario
@RequiresApi(Build.VERSION_CODES.O)
data class Day(
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val date: LocalDate,
    val dayNumber: Int,
    val activities: List<Activity> = emptyList()
)

// Modelo para actividades dentro de un día
@RequiresApi(Build.VERSION_CODES.O)
data class Activity(
    val id: String = UUID.randomUUID().toString(),
    val dayId: String,
    val name: String,
    val description: String = "",
    val startTime: LocalTime,
    val endTime: LocalTime? = null,
    val location: String = ""
) 