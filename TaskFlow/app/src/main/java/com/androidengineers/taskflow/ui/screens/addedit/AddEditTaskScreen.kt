package com.androidengineers.taskflow.ui.screens.addedit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidengineers.taskflow.domain.model.Category
import com.androidengineers.taskflow.domain.model.Priority
import com.androidengineers.taskflow.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTaskScreen(
    taskId: Long = -1L,
    onNavigateBack: () -> Unit,
    viewModel: AddEditTaskViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditMode = taskId != -1L

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.dueDate)
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDate by remember(uiState.dueDate) {
        derivedStateOf {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(uiState.dueDate))
        }
    }

    val isSaveEnabled by remember(uiState.title) {
        derivedStateOf { uiState.title.isNotBlank() }
    }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onDueDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (isEditMode) "Edit Task" else "New Task",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                actions = {
                    if (isEditMode) {
                        TextButton(onClick = { viewModel.saveTask(onNavigateBack) }, enabled = isSaveEnabled) {
                            Text("Save", color = BluePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Task Name
            Column {
                Text(
                    "TASK NAME",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    placeholder = { Text("What needs to be done?", color = TextSecondary.copy(alpha = 0.3f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary.copy(alpha = 0.1f),
                        unfocusedBorderColor = BluePrimary.copy(alpha = 0.05f),
                        focusedContainerColor = BackgroundLight,
                        unfocusedContainerColor = BackgroundLight
                    )
                )
            }

            // Description
            Column {
                Text(
                    "DESCRIPTION",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = { Text("Add details, subtasks, or links...", color = TextSecondary.copy(alpha = 0.3f)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary.copy(alpha = 0.1f),
                        unfocusedBorderColor = BluePrimary.copy(alpha = 0.05f),
                        focusedContainerColor = BackgroundLight,
                        unfocusedContainerColor = BackgroundLight
                    )
                )
            }

            // Category Selection
            Column {
                Text("CATEGORY", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary.copy(alpha = 0.5f), fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Category.entries.forEach { category ->
                        val isSelected = uiState.category == category
                        Surface(
                            onClick = { viewModel.onCategoryChange(category) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) BluePrimary.copy(alpha = 0.1f) else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) BluePrimary.copy(alpha = 0.2f) else TextSecondary.copy(alpha = 0.1f)),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FormatListBulleted,
                                    contentDescription = null,
                                    tint = if (isSelected) BluePrimary else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    category.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) BluePrimary else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Details (Due Date, List, Remind Me)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("DETAILS", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary.copy(alpha = 0.5f), fontWeight = FontWeight.Bold))
                
                DetailItem(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Due Date",
                    value = formattedDate,
                    onClick = { showDatePicker = true },
                    valueColor = BluePrimary
                )
                
                DetailItem(
                    icon = Icons.Outlined.FormatListBulleted,
                    label = "List",
                    value = uiState.category.title,
                    onClick = { }
                )
                
                DetailRow(
                    icon = Icons.Outlined.NotificationsNone,
                    label = "Remind Me",
                    trailing = {
                        Switch(
                            checked = true, 
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BluePrimary)
                        )
                    }
                )
            }

            // Priority
            Column {
                Text("PRIORITY", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary.copy(alpha = 0.5f), fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Priority.entries.forEach { priority ->
                        val isSelected = uiState.priority == priority
                        Surface(
                            onClick = { viewModel.onPriorityChange(priority) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) BluePrimary else Color.Transparent,
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    priority.level,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Add Attachment
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { }
            ) {
                Icon(Icons.Outlined.AttachFile, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Attachment", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isEditMode) {
                Button(
                    onClick = { viewModel.saveTask(onNavigateBack) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary.copy(alpha = 0.2f), contentColor = BluePrimary),
                    enabled = isSaveEnabled
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Save Task", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 180f))
                    }
                }
            } else {
                 OutlinedButton(
                    onClick = { /* Delete Task logic */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PriorityHighText),
                    border = BorderStroke(1.dp, PriorityHighText.copy(alpha = 0.2f))
                ) {
                    Text("Delete Task", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    valueColor: Color = TextSecondary
) {
    DetailRow(icon = icon, label = label, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = if (valueColor == BluePrimary) BluePrimary.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    value, 
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(color = valueColor)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary.copy(alpha = 0.3f), modifier = Modifier.size(16.dp).graphicsLayer(rotationZ = 180f))
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = BackgroundLight,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(icon, contentDescription = null, tint = if (label == "Remind Me") Color(0xFFFBC02D) else BluePrimary, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
        trailing()
    }
}
