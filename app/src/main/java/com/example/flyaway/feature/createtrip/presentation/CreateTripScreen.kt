package com.example.flyaway.feature.createtrip.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flyaway.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateTripViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    
    // Efecto para manejar la navegación una vez creado el viaje
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            // Navegar a la pantalla anterior después de crear el viaje
            onNavigateBack()
            viewModel.onEvent(CreateTripEvent.OnResetState)
        }
    }
    
    // Efecto para mostrar errores en el snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
        }
    }
    
    // Diálogo para seleccionar la fecha de inicio
    if (state.showStartDatePicker) {
        ShowDatePicker(
            initialDate = state.startDate,
            isStartDate = true,
            onDateSelected = { viewModel.onEvent(CreateTripEvent.OnDateSelected(it, true)) },
            onDismiss = { viewModel.onEvent(CreateTripEvent.OnDatePickerDismiss) },
            minDate = LocalDate.now()
        )
    }
    
    // Diálogo para seleccionar la fecha de fin
    if (state.showEndDatePicker) {
        ShowDatePicker(
            initialDate = state.endDate,
            isStartDate = false,
            onDateSelected = { viewModel.onEvent(CreateTripEvent.OnDateSelected(it, false)) },
            onDismiss = { viewModel.onEvent(CreateTripEvent.OnDatePickerDismiss) },
            minDate = state.startDate ?: LocalDate.now()
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_trip)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CreateTripContent(
                state = state,
                onEvent = viewModel::onEvent,
                dateFormatter = dateFormatter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
            
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTripContent(
    state: CreateTripState,
    onEvent: (CreateTripEvent) -> Unit,
    dateFormatter: DateTimeFormatter,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo para el nombre del viaje
        OutlinedTextField(
            value = state.name,
            onValueChange = { onEvent(CreateTripEvent.OnNameChange(it)) },
            label = { Text(stringResource(R.string.trip_name)) },
            isError = state.nameError != null,
            supportingText = state.nameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        
        // Campo para el destino
        OutlinedTextField(
            value = state.destination,
            onValueChange = { onEvent(CreateTripEvent.OnDestinationChange(it)) },
            label = { Text(stringResource(R.string.destination)) },
            isError = state.destinationError != null,
            supportingText = state.destinationError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        
        // Campo para la fecha de inicio
        OutlinedTextField(
            value = state.startDate?.format(dateFormatter) ?: "",
            onValueChange = { /* No se permite entrada directa */ },
            label = { Text(stringResource(R.string.start_date)) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.start_date),
                    modifier = Modifier.clickable { onEvent(CreateTripEvent.OnStartDateClick) }
                )
            },
            isError = state.dateError != null && state.startDate == null,
            supportingText = if (state.dateError != null && state.startDate == null) {
                { Text(stringResource(R.string.required_start_date)) }
            } else null,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Campo para la fecha de fin
        OutlinedTextField(
            value = state.endDate?.format(dateFormatter) ?: "",
            onValueChange = { /* No se permite entrada directa */ },
            label = { Text(stringResource(R.string.end_date)) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.end_date),
                    modifier = Modifier.clickable { onEvent(CreateTripEvent.OnEndDateClick) }
                )
            },
            isError = state.dateError != null && state.endDate == null,
            supportingText = if (state.dateError != null && state.endDate == null) {
                { Text(stringResource(R.string.required_end_date)) }
            } else null,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Mensaje de error para las fechas (cuando hay conflicto entre ellas)
        if (state.dateError != null && state.startDate != null && state.endDate != null) {
            Text(
                text = state.dateError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón para crear el viaje
        Button(
            onClick = { onEvent(CreateTripEvent.OnCreateTrip) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowDatePicker(
    initialDate: LocalDate?,
    isStartDate: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    minDate: LocalDate
) {
    val initialMillis = initialDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        ?: System.currentTimeMillis()
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                // Permitir fechas desde hoy en adelante, incluyendo fines de semana
                return !date.isBefore(LocalDate.now())
            }
        }
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    if (isStartDate) 
                        stringResource(R.string.select_start_date)
                    else 
                        stringResource(R.string.select_end_date)
                )
            },
            showModeToggle = false,
            headline = {
                Text(
                    if (isStartDate) 
                        stringResource(R.string.start_date)
                    else 
                        stringResource(R.string.end_date)
                )
            }
        )
    }
} 