package com.example.travelplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelplanner.R
import com.example.travelplanner.data.repository.TripRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(navController: NavController) {
    val trips = TripRepository.getAllTrips()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Trips") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(trips) { trip ->
                        TripItem(trip.name, trip.id) {
                            navController.navigate("trip_detail/${trip.id}")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TripItem(title: String, tripId: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
