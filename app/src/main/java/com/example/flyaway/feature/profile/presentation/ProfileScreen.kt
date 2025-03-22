package com.example.flyaway.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flyaway.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showSaveConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (state.isEditing) {
                        IconButton(onClick = { 
                            viewModel.onEvent(ProfileEvent.OnSaveProfile)
                            showSaveConfirmation = true
                        }) {
                            Icon(Icons.Default.Save, contentDescription = stringResource(R.string.save))
                        }
                    } else {
                        IconButton(onClick = { viewModel.onEvent(ProfileEvent.OnEditProfile) }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil
            Surface(
                shape = CircleShape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .padding(8.dp)
                )
            }
            
            if (state.isEditing) {
                TextButton(
                    onClick = { viewModel.onEvent(ProfileEvent.OnChangePhoto) }
                ) {
                    Text(text = stringResource(R.string.change_photo))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Información de perfil
            ProfileField(
                label = stringResource(R.string.profile_name),
                value = state.name,
                onValueChange = { viewModel.onEvent(ProfileEvent.OnNameChange(it)) },
                icon = Icons.Default.Person,
                isEditing = state.isEditing
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileField(
                label = stringResource(R.string.profile_email),
                value = state.email,
                onValueChange = { viewModel.onEvent(ProfileEvent.OnEmailChange(it)) },
                icon = Icons.Default.Email,
                isEditing = state.isEditing
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileField(
                label = stringResource(R.string.profile_phone),
                value = state.phone,
                onValueChange = { viewModel.onEvent(ProfileEvent.OnPhoneChange(it)) },
                icon = Icons.Default.Phone,
                isEditing = state.isEditing
            )
        }
        
        if (showSaveConfirmation) {
            AlertDialog(
                onDismissRequest = { showSaveConfirmation = false },
                title = { Text(stringResource(R.string.profile)) },
                text = { Text(stringResource(R.string.profile_saved)) },
                confirmButton = {
                    Button(
                        onClick = { 
                            showSaveConfirmation = false
                            viewModel.onEvent(ProfileEvent.OnConfirmSave)
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isEditing: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    leadingIcon = { Icon(icon, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
} 