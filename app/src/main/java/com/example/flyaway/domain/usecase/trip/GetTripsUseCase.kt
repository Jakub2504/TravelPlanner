package com.example.flyaway.domain.usecase.trip

import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener todos los viajes.
 * Sigue el principio de responsabilidad única, encapsulando una operación específica.
 */
class GetTripsUseCase @Inject constructor(
    private val tripRepository: ITripRepository
) {
    operator fun invoke(): Flow<List<Trip>> {
        return tripRepository.getAllTrips()
    }
} 