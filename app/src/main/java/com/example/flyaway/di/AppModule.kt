package com.example.flyaway.di

import android.content.Context
import com.example.flyaway.core.data.preferences.PreferencesRepository
import com.example.flyaway.data.repository.TripRepositoryImpl
import com.example.flyaway.domain.repository.ITripRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo principal de Dagger-Hilt para proporcionar todas las dependencias de la aplicación.
 */
@Module(includes = [AppModule.BindsModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // Dependencias proporcionadas con @Provides
    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }
    
    /**
     * Submódulo para bindings con @Binds
     */
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class BindsModule {
        
        /**
         * Proporciona la implementación del repositorio de viajes.
         */
        @Binds
        @Singleton
        abstract fun bindTripRepository(
            tripRepositoryImpl: TripRepositoryImpl
        ): ITripRepository
    }
} 