package com.example.flyaway.domain.usecase.trip

import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * Caso de uso para guardar un viaje (crear nuevo o actualizar existente).
 */
class SaveTripUseCase @Inject constructor(
    private val tripRepository: ITripRepository
) {
    suspend operator fun invoke(trip: Trip): Trip {
        // Se podría añadir lógica de validación aquí si fuera necesario
        return tripRepository.saveTrip(trip)
    }
} 