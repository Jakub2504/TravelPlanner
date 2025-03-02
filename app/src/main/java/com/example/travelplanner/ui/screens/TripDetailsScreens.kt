package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.*
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
import com.example.travelplanner.data.models.Trip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(navController: NavController, trip: Trip) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Text("Destination: ${trip.destination}", modifier = Modifier.padding(8.dp))
                Text("Start Date: ${trip.startDate}", modifier = Modifier.padding(8.dp))
                Text("End Date: ${trip.endDate}", modifier = Modifier.padding(8.dp))
                Text("Notes: ${trip.notes ?: "No notes available"}", modifier = Modifier.padding(8.dp))
            }
        }
    )
}

