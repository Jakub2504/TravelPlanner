package com.example.flyaway.domain.usecase.trip

import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener un viaje específico por su ID.
 */
class GetTripByIdUseCase @Inject constructor(
    private val tripRepository: ITripRepository
) {
    operator fun invoke(tripId: String): Flow<Trip?> {
        return tripRepository.getTripById(tripId)
    }
} 