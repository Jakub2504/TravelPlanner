package com.example.flyaway.core.navigation

sealed class AppDestinations(val route: String) {
    object Splash : AppDestinations("splash")
    object Login : AppDestinations("login")
    object Home : AppDestinations("home")
    object Settings : AppDestinations("settings")
    object LanguageSettings : AppDestinations("language_settings")
    object AboutUs : AppDestinations("about_us")
    object Terms : AppDestinations("terms")
    object CreateTrip : AppDestinations("create_trip")
    object Profile : AppDestinations("profile")
    
    object TripDetails : AppDestinations("trip_details/{tripId}") {
        const val TRIP_ID_PARAM = "tripId"
        
        fun createRoute(tripId: String): String {
            return "trip_details/$tripId"
        }
    }
} 