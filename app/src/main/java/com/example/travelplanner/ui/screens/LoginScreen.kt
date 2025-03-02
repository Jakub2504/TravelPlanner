package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelplanner.data.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Login") }) },
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID") },
                    modifier = Modifier.padding(16.dp)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.padding(16.dp)
                )

                if (loginError) {
                    Text(
                        "Invalid credentials, try again.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Button(
                    onClick = {
                        if (UserRepository.validateUser(userId, password)) {
                            navController.navigate("splash") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            loginError = true
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Login")
                }
            }
        }
    )
}

