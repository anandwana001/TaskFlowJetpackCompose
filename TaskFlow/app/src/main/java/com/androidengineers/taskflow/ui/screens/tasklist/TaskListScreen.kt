package com.androidengineers.taskflow.ui.screens.tasklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidengineers.taskflow.domain.model.Category
import com.androidengineers.taskflow.domain.model.Task
import com.androidengineers.taskflow.ui.components.TaskItem
import com.androidengineers.taskflow.ui.theme.BluePrimary
import com.androidengineers.taskflow.ui.theme.TextPrimary
import com.androidengineers.taskflow.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    viewModel: TaskListViewModel = koinViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    val filteredTasks by remember(tasks, searchQuery, selectedCategory) {
        derivedStateOf {
            tasks.filter {
                (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true)) &&
                        (selectedCategory == null || it.category == selectedCategory)
            }
        }
    }

    val todayTasks by remember(filteredTasks) {
        derivedStateOf {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val tomorrow = today + 24 * 60 * 60 * 1000
            filteredTasks.filter { it.dueDate in today until tomorrow }
        }
    }

    val tomorrowTasks by remember(filteredTasks) {
        derivedStateOf {
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayAfterTomorrow = tomorrow + 24 * 60 * 60 * 1000
            filteredTasks.filter { it.dueDate in tomorrow until dayAfterTomorrow }
        }
    }

    val otherTasks by remember(filteredTasks, todayTasks, tomorrowTasks) {
        derivedStateOf {
            filteredTasks.filter { it !in todayTasks && it !in tomorrowTasks }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = BluePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
                    label = { Text("Alerts") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date()),
                        style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                    )
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = BluePrimary.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Profile",
                        tint = BluePrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search your tasks...", color = TextSecondary.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary.copy(alpha = 0.5f)) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CategoryChip(
                        title = "All",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(Category.entries) { category ->
                    CategoryChip(
                        title = category.title,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Task List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (todayTasks.isNotEmpty()) {
                    item {
                        Text(
                            "TODAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(todayTasks, key = { it.id }) { task ->
                        SwipeToDeleteTask(
                            task = task,
                            onTaskClick = { onEditTask(task.id) },
                            onDelete = { viewModel.deleteTask(task) },
                            onCompleteClick = { viewModel.toggleTaskCompletion(task) }
                        )
                    }
                }

                if (tomorrowTasks.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "TOMORROW",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(tomorrowTasks, key = { it.id }) { task ->
                        SwipeToDeleteTask(
                            task = task,
                            onTaskClick = { onEditTask(task.id) },
                            onDelete = { viewModel.deleteTask(task) },
                            onCompleteClick = { viewModel.toggleTaskCompletion(task) }
                        )
                    }
                }
                
                // Other tasks if needed or if today/tomorrow are empty but filtered results exist
                if (otherTasks.isNotEmpty()) {
                     item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "OTHER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(otherTasks, key = { it.id }) { task ->
                        SwipeToDeleteTask(
                            task = task,
                            onTaskClick = { onEditTask(task.id) },
                            onDelete = { viewModel.deleteTask(task) },
                            onCompleteClick = { viewModel.toggleTaskCompletion(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) BluePrimary else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder,
        modifier = Modifier.height(40.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = if (isSelected) Color.White else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteTask(
    task: Task,
    onTaskClick: (Task) -> Unit,
    onDelete: () -> Unit,
    onCompleteClick: (Task) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.1f)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
                    .background(color, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        TaskItem(
            task = task,
            onTaskClick = onTaskClick,
            onDeleteClick = { onDelete() },
            onCompleteClick = onCompleteClick
        )
    }
}
