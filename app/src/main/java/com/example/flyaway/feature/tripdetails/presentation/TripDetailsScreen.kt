package com.example.flyaway.feature.tripdetails.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flyaway.R
import com.example.flyaway.core.data.model.Activity
import com.example.flyaway.core.data.model.Day
import com.example.flyaway.core.data.model.Trip
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TripDetailsScreen(
    tripId: String,
    viewModel: TripDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    // Efecto para cargar el viaje usando el ID
    LaunchedEffect(tripId) {
        viewModel.onEvent(TripDetailsEvent.LoadTrip(tripId))
    }
    
    // Efecto para navegar a Home cuando el viaje se ha eliminado
    LaunchedEffect(state.tripDeleted) {
        if (state.tripDeleted) {
            onNavigateToHome()
        }
    }
    
    // Diálogo para añadir un nuevo día
    if (state.showAddDayDialog) {
        DatePickerDialog(
            title = stringResource(R.string.add_day),
            initialDate = state.newDayDate,
            onDateSelected = { viewModel.onEvent(TripDetailsEvent.OnDayDateChange(it)) },
            onConfirm = { viewModel.onEvent(TripDetailsEvent.OnSaveDay) },
            onDismiss = { viewModel.onEvent(TripDetailsEvent.OnDayDialogDismiss) }
        )
    }
    
    // Diálogo para editar un día
    if (state.showEditDayDialog && state.dayToEdit != null) {
        DatePickerDialog(
            title = stringResource(R.string.edit_day),
            initialDate = state.newDayDate,
            onDateSelected = { viewModel.onEvent(TripDetailsEvent.OnDayDateChange(it)) },
            onConfirm = { viewModel.onEvent(TripDetailsEvent.OnSaveDay) },
            onDismiss = { viewModel.onEvent(TripDetailsEvent.OnDayDialogDismiss) }
        )
    }
    
    // Diálogo para eliminar un día
    if (state.showDeleteDayDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(TripDetailsEvent.OnDeleteDayDismiss) },
            title = { Text(stringResource(R.string.delete_day)) },
            text = { Text(stringResource(R.string.delete_day_confirmation)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(TripDetailsEvent.OnDeleteDayConfirm) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.onEvent(TripDetailsEvent.OnDeleteDayDismiss) }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    // Diálogo para añadir/editar actividad
    if (state.showAddActivityDialog || state.showEditActivityDialog) {
        ActivityDialog(
            title = if (state.showEditActivityDialog) stringResource(R.string.edit_activity) else stringResource(R.string.add_activity),
            name = state.newActivityName,
            description = state.newActivityDescription,
            location = state.newActivityLocation,
            startTime = state.newActivityStartTime,
            endTime = state.newActivityEndTime,
            onNameChange = { viewModel.onEvent(TripDetailsEvent.OnActivityNameChange(it)) },
            onDescriptionChange = { viewModel.onEvent(TripDetailsEvent.OnActivityDescriptionChange(it)) },
            onLocationChange = { viewModel.onEvent(TripDetailsEvent.OnActivityLocationChange(it)) },
            onStartTimeChange = { viewModel.onEvent(TripDetailsEvent.OnActivityStartTimeChange(it)) },
            onEndTimeChange = { viewModel.onEvent(TripDetailsEvent.OnActivityEndTimeChange(it)) },
            onConfirm = { viewModel.onEvent(TripDetailsEvent.OnSaveActivity) },
            onDismiss = { viewModel.onEvent(TripDetailsEvent.OnActivityDialogDismiss) },
            errorMessage = state.error
        )
    }
    
    // Diálogo para eliminar una actividad
    if (state.showDeleteActivityDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(TripDetailsEvent.OnDeleteActivityDismiss) },
            title = { Text(stringResource(R.string.delete_activity)) },
            text = { Text(stringResource(R.string.delete_activity_confirmation)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(TripDetailsEvent.OnDeleteActivityConfirm) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.onEvent(TripDetailsEvent.OnDeleteActivityDismiss) }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    // Diálogo para eliminar el viaje
    if (state.showDeleteTripDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(TripDetailsEvent.OnDeleteTripDismiss) },
            title = { Text(stringResource(R.string.delete_trip)) },
            text = { Text(stringResource(R.string.delete_trip_confirmation)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(TripDetailsEvent.OnDeleteTripConfirm) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.onEvent(TripDetailsEvent.OnDeleteTripDismiss) }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    // Diálogo para editar el viaje
    if (state.showEditTripDialog && state.trip != null) {
        EditTripDialog(
            trip = state.trip,
            onNameChange = { viewModel.onEvent(TripDetailsEvent.OnTripNameChange(it)) },
            onDestinationChange = { viewModel.onEvent(TripDetailsEvent.OnTripDestinationChange(it)) },
            onStartDateChange = { viewModel.onEvent(TripDetailsEvent.OnTripStartDateChange(it)) },
            onEndDateChange = { viewModel.onEvent(TripDetailsEvent.OnTripEndDateChange(it)) },
            onConfirm = { viewModel.onEvent(TripDetailsEvent.OnEditTripConfirm) },
            onDismiss = { viewModel.onEvent(TripDetailsEvent.OnEditTripDismiss) }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.trip_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(TripDetailsEvent.OnEditTripClick) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_trip)
                        )
                    }
                    IconButton(onClick = { viewModel.onEvent(TripDetailsEvent.OnDeleteTripClick) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_trip)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            TripDetailsContent(
                state = state,
                paddingValues = paddingValues,
                viewModel = viewModel
            )
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun TripDetailsContent(
    state: TripDetailsState,
    paddingValues: PaddingValues,
    viewModel: TripDetailsViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.error != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.error ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.onEvent(TripDetailsEvent.LoadTrip(state.trip?.id ?: "")) }) {
                    Text(text = stringResource(R.string.retry))
                }
            }
        } else {
            if (state.trip != null) {
                displayTripContent(state.trip!!, viewModel)
            } else {
                // Si no hay viaje y no está cargando ni hay error, mostrar mensaje
                Text(
                    text = stringResource(R.string.no_trip_found),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun displayTripContent(
    trip: Trip,
    viewModel: TripDetailsViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Información del viaje
        item {
            TripHeader(trip = trip)
        }
        
        // Itinerario
        item {
            Text(
                text = stringResource(R.string.itinerary),
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        // Si no hay días, mostrar mensaje y botón para añadir
        if (trip.days.isEmpty()) {
            item {
                NoDaysMessage(
                    onAddClick = { viewModel.onEvent(TripDetailsEvent.OnAddDayClick) }
                )
            }
        } else {
            // Mostrar los días ordenados por número
            items(trip.days.sortedBy { it.dayNumber }) { day ->
                DayCard(
                    day = day,
                    dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy"),
                    onClick = { /* No acción necesaria aquí, ya que tenemos botones dedicados */ },
                    onAddActivityClick = { viewModel.onEvent(TripDetailsEvent.OnAddActivityClick(day.id)) },
                    onEditClick = { viewModel.onEvent(TripDetailsEvent.OnEditDayClick(day)) },
                    onDeleteClick = { viewModel.onEvent(TripDetailsEvent.OnDeleteDayClick(day)) },
                    onEditActivityClick = { activity -> viewModel.onEvent(TripDetailsEvent.OnEditActivityClick(activity)) },
                    onDeleteActivityClick = { activity -> viewModel.onEvent(TripDetailsEvent.OnDeleteActivityClick(activity)) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun TripHeader(
    trip: Trip
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = trip.name,
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = trip.destination,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${trip.startDate.format(dateFormatter)} - ${trip.endDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Duración en días
            val duration = ChronoUnit.DAYS.between(trip.startDate, trip.endDate) + 1
            Text(
                text = stringResource(
                    id = R.string.trip_duration,
                    formatArgs = arrayOf(duration)
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
internal fun NoDaysMessage(
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.no_days_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.add_day))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun DayCard(
    day: Day,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit,
    onAddActivityClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditActivityClick: (Activity) -> Unit,
    onDeleteActivityClick: (Activity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            R.string.day_number_date,
                            day.dayNumber,
                            day.date.format(dateFormatter)
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                // Botones de editar/eliminar
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Mostrar actividades si las hay
            if (day.activities.isNotEmpty()) {
                day.activities.forEach { activity ->
                    ActivityItem(
                        activity = activity,
                        timeFormatter = DateTimeFormatter.ofPattern("HH:mm"),
                        onEditClick = { onEditActivityClick(activity) },
                        onDeleteClick = { onDeleteActivityClick(activity) }
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.no_activities),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Botón para añadir actividad
            Button(
                onClick = onAddActivityClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.add_activity))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ActivityItem(
    activity: Activity,
    timeFormatter: DateTimeFormatter,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.titleSmall
                )
                
                activity.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${activity.startTime.format(timeFormatter)} - ${activity.endTime?.format(timeFormatter) ?: ""}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                activity.location?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Botones de editar/eliminar
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    title: String,
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = initialDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        ?: System.currentTimeMillis()
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                // Permitir fechas desde hoy en adelante, incluyendo fines de semana
                return !date.isBefore(LocalDate.now())
            }
        }
    )
    
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                        onConfirm()
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
            title = { Text(title) },
            showModeToggle = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityDialog(
    title: String,
    name: String,
    description: String,
    location: String,
    startTime: LocalTime?,
    endTime: LocalTime?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onStartTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Mostrar mensaje de error si existe
                errorMessage?.let { error ->
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Formulario
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.activity_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text(stringResource(R.string.location)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Selección de hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Hora de inicio
                    var showStartTimePicker by remember { mutableStateOf(false) }
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.start_time),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                        val startTimeText = startTime?.format(timeFormatter) ?: "--:--"
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { showStartTimePicker = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = startTimeText)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Hora de fin
                    var showEndTimePicker by remember { mutableStateOf(false) }
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.end_time),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                        val endTimeText = endTime?.format(timeFormatter) ?: "--:--"
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { showEndTimePicker = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = endTimeText)
                            }
                        }
                    }
                    
                    // Time pickers
                    if (showStartTimePicker) {
                        TimePickerDialog(
                            onConfirm = { hour, minute ->
                                onStartTimeChange(LocalTime.of(hour, minute))
                                showStartTimePicker = false
                            },
                            onDismiss = { showStartTimePicker = false },
                            initialTime = startTime ?: LocalTime.now()
                        )
                    }
                    
                    if (showEndTimePicker) {
                        TimePickerDialog(
                            onConfirm = { hour, minute ->
                                onEndTimeChange(LocalTime.of(hour, minute))
                                showEndTimePicker = false
                            },
                            onDismiss = { showEndTimePicker = false },
                            initialTime = endTime ?: LocalTime.now().plusHours(1)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onConfirm,
                        enabled = name.isNotBlank() && startTime != null && endTime != null
                    ) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    initialTime: LocalTime
) {
    var hour by remember { mutableStateOf(initialTime.hour) }
    var minute by remember { mutableStateOf(initialTime.minute) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.select_time),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Selector de hora
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Selector de hora
                    NumberPicker(
                        value = hour,
                        onValueChange = { hour = it },
                        range = 0..23
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Selector de minutos
                    NumberPicker(
                        value = minute,
                        onValueChange = { minute = it },
                        range = 0..59
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(onClick = { onConfirm(hour, minute) }) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(60.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 4.dp)
    ) {
        // Botón para incrementar
        IconButton(
            onClick = {
                if (value < range.last) {
                    onValueChange(value + 1)
                }
            },
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "▲",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Valor actual
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Botón para decrementar
        IconButton(
            onClick = {
                if (value > range.first) {
                    onValueChange(value - 1)
                }
            },
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "▼",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CircularProgressIndicator(modifier: Modifier = Modifier) {
    androidx.compose.material3.CircularProgressIndicator(modifier = modifier)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTripDialog(
    trip: Trip?,
    onNameChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // Asegurarse de que trip no es nulo
    if (trip == null) return
    
    var tripName by remember { mutableStateOf(trip.name) }
    var tripDestination by remember { mutableStateOf(trip.destination) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(trip.startDate) }
    var endDate by remember { mutableStateOf(trip.endDate) }
    
    // Efectos para notificar cambios
    LaunchedEffect(tripName) {
        onNameChange(tripName)
    }
    
    LaunchedEffect(tripDestination) {
        onDestinationChange(tripDestination)
    }
    
    LaunchedEffect(startDate) {
        onStartDateChange(startDate)
    }
    
    LaunchedEffect(endDate) {
        onEndDateChange(endDate)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.edit_trip),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Nombre del viaje
                OutlinedTextField(
                    value = tripName,
                    onValueChange = { tripName = it },
                    label = { Text(stringResource(R.string.trip_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Destino
                OutlinedTextField(
                    value = tripDestination,
                    onValueChange = { tripDestination = it },
                    label = { Text(stringResource(R.string.destination)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Fechas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Fecha inicio
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.start_date),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { showStartDatePicker = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = startDate.format(dateFormatter))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Fecha fin
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.end_date),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { showEndDatePicker = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = endDate.format(dateFormatter))
                            }
                        }
                    }
                }
                
                // Date pickers
                if (showStartDatePicker) {
                    DatePickerDialog(
                        title = stringResource(R.string.select_start_date),
                        initialDate = startDate,
                        onDateSelected = { 
                            startDate = it
                            // Si la fecha de inicio es posterior a la de fin, actualizar la de fin
                            if (startDate.isAfter(endDate)) {
                                endDate = startDate
                            }
                        },
                        onConfirm = { showStartDatePicker = false },
                        onDismiss = { showStartDatePicker = false }
                    )
                }
                
                if (showEndDatePicker) {
                    DatePickerDialog(
                        title = stringResource(R.string.select_end_date),
                        initialDate = endDate,
                        onDateSelected = { 
                            endDate = it
                            // Si la fecha de fin es anterior a la de inicio, actualizar la de inicio
                            if (endDate.isBefore(startDate)) {
                                startDate = endDate
                            }
                        },
                        onConfirm = { showEndDatePicker = false },
                        onDismiss = { showEndDatePicker = false }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onConfirm,
                        enabled = tripName.isNotBlank() && tripDestination.isNotBlank()
                    ) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
} 