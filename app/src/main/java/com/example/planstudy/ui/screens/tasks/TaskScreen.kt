package com.example.planstudy.ui.screens.tasks

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planstudy.data.AppDatabase
import com.example.planstudy.data.model.Task
import com.example.planstudy.ui.components.TaskItem
import com.example.planstudy.ui.tasks.TaskViewModel
import com.example.planstudy.ui.tasks.TaskViewModelFactory
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val taskDao = remember { database.taskDao() }
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(taskDao))
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Task.Priority.MEDIUM) }

    // Date/Time Picker State
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.NOON) }

    val currentDeadline by remember(selectedDate, selectedTime) {
        derivedStateOf {
            LocalDateTime.of(selectedDate, selectedTime)
        }
    }

    // Show toast when task is deleted
    LaunchedEffect(viewModel.tasks.value) {
        if (viewModel.showDeleteToast) {
            Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
            viewModel.showDeleteToast = false
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("Add Task") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),

        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = {
                            viewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
                        },
                        onDelete = {
                            viewModel.deleteTask(task)
                            Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Add Task Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add New Task") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newTaskDescription,
                            onValueChange = { newTaskDescription = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Priority", style = MaterialTheme.typography.labelMedium)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Task.Priority.values().forEach { priority ->
                                FilterChip(
                                    selected = selectedPriority == priority,
                                    onClick = { selectedPriority = priority },
                                    label = { Text(priority.name) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Deadline: ${currentDeadline.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { dateDialogState.show() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Set Date")
                            }
                            Button(
                                onClick = { timeDialogState.show() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Set Time")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                viewModel.addTask(
                                    Task(
                                        title = newTaskTitle,
                                        description = newTaskDescription,
                                        deadline = currentDeadline,
                                        priority = selectedPriority,
                                        subject = "General"
                                    )
                                )
                                Toast.makeText(context, "Task added", Toast.LENGTH_SHORT).show()
                                // Reset form
                                newTaskTitle = ""
                                newTaskDescription = ""
                                selectedPriority = Task.Priority.MEDIUM
                                selectedDate = LocalDate.now()
                                selectedTime = LocalTime.NOON
                                showDialog = false
                            }
                        },
                        enabled = newTaskTitle.isNotBlank()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Date Picker Dialog
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "OK")
                negativeButton(text = "Cancel")
            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Select deadline date",
                allowedDateValidator = { it >= LocalDate.now() }
            ) { newDate ->
                selectedDate = newDate
            }
        }

        // Time Picker Dialog
        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(text = "OK")
                negativeButton(text = "Cancel")
            }
        ) {
            timepicker(
                initialTime = LocalTime.NOON,
                title = "Select deadline time"
            ) { newTime ->
                selectedTime = newTime
            }
        }
    }
}