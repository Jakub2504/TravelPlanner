package com.example.flyaway.feature.settings.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flyaway.MainActivity
import com.example.flyaway.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: LanguageSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val mainActivity = context.findActivity() as? MainActivity
    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto para reiniciar la actividad si el idioma ha cambiado
    LaunchedEffect(state.languageChanged) {
        if (state.languageChanged) {
            showConfirmation = true
            // Reiniciar el estado para evitar que el diálogo aparezca de nuevo
            viewModel.resetLanguageChanged()
        }
    }
    
    // Mostrar errores si los hay
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.language)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.select_language),
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                LanguageOption(
                    name = stringResource(R.string.language_english),
                    selected = state.selectedLanguage == "en",
                    onClick = { viewModel.onLanguageSelected("en") }
                )
                
                LanguageOption(
                    name = stringResource(R.string.language_spanish),
                    selected = state.selectedLanguage == "es",
                    onClick = { viewModel.onLanguageSelected("es") }
                )
                
                LanguageOption(
                    name = stringResource(R.string.language_catalan),
                    selected = state.selectedLanguage == "ca",
                    onClick = { viewModel.onLanguageSelected("ca") }
                )
                
                LanguageOption(
                    name = stringResource(R.string.language_polish),
                    selected = state.selectedLanguage == "pl",
                    onClick = { viewModel.onLanguageSelected("pl") }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { viewModel.saveLanguage() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        }
        
        if (showConfirmation) {
            AlertDialog(
                onDismissRequest = { 
                    showConfirmation = false
                },
                title = { Text(stringResource(R.string.language)) },
                text = { Text(stringResource(R.string.language_changed)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmation = false
                            // Usar el método mejorado para aplicar el idioma y reiniciar
                            mainActivity?.applyLanguageAndRecreate(state.selectedLanguage)
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            )
        }
    }
}

// Función de extensión para encontrar la actividad desde un contexto
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
private fun LanguageOption(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        )
        
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
} 