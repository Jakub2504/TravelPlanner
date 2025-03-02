package com.example.travelplanner.data.repository

import com.example.travelplanner.data.models.Trip

object TripRepository {
    private val trips = mutableListOf(
        Trip(1, "Summer Vacation", "Hawaii", "2025-06-10", "2025-06-20", "Relaxing beach trip."),
        Trip(2, "Business Conference", "New York", "2025-07-05", "2025-07-10", "Networking and meetings."),
        Trip(3, "Backpacking Adventure", "Thailand", "2025-08-15", "2025-09-05", "Exploring nature and culture.")
    )

    fun getAllTrips(): List<Trip> = trips
    fun getTripById(id: Int): Trip? = trips.find { it.id == id }
}
