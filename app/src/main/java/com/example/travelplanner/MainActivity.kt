package com.example.travelplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.ui.components.AppBar
import com.example.travelplanner.ui.screens.*
import com.example.travelplanner.data.repository.UserRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelPlannerApp()
        }
    }
}

@Composable
fun TravelPlannerApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { AppBar(title = "Travel Planner") }
    ) { innerPadding ->
        NavigationGraph(navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = "login", modifier = modifier) {
        composable("login") { LoginScreen(navController) }
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("trip_list") { TripListScreen(navController) }
        composable("about") { AboutScreen(navController) }
        composable("terms") { TermsAndConditionsScreen(navController) }
        composable("trip_detail/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: 0
            val trip = TripRepository.getTripById(tripId)
            if (trip != null) {
                TripDetailScreen(navController, trip)
            } else {
                Text("Trip not found")
            }
        }
    }
}


