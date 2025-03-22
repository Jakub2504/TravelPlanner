package com.example.flyaway.domain.usecase.trip

import com.example.flyaway.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * Caso de uso para eliminar un viaje por su ID.
 */
class DeleteTripUseCase @Inject constructor(
    private val tripRepository: ITripRepository
) {
    suspend operator fun invoke(tripId: String) {
        tripRepository.deleteTrip(tripId)
    }
} 