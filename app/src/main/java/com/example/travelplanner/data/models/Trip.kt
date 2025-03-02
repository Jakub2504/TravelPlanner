package com.example.travelplanner.data.models

import java.util.Date

// Trip Data Model
data class Trip(
    val id: Int,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val notes: String? = null
)
