package com.example.planstudy.ui.screens.pomodoro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planstudy.data.model.Task
import com.example.planstudy.ui.tasks.TaskViewModel
import com.example.planstudy.ui.tasks.TaskViewModelFactory
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planstudy.data.AppDatabase

@Composable
fun PomodoroScreen(modifier: Modifier) {
    // Initial states
    var isRunning by remember { mutableStateOf(false) }
    var isStudyTime by remember { mutableStateOf(true) }
    var timeLeft by remember { mutableStateOf(1500) } // Default: 25 min
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var userMinutes by remember { mutableStateOf("25") }
    var userSeconds by remember { mutableStateOf("00") }

    // ViewModel setup for task management
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(taskDao = AppDatabase.getDatabase(context).taskDao()))
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val incompleteTasks = tasks.filter { !it.isCompleted }

    // Timer logic
    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0 && selectedTask != null) {
            isStudyTime = !isStudyTime
            timeLeft = if (isStudyTime) 1500 else 300 // Switch to break/study
            selectedTask?.let {
                val updatedTask = it.copy(isCompleted = true)  // Mark task as completed
                viewModel.updateTask(updatedTask)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Timer and task status display
        Text(
            text = if (isStudyTime) "Study Time" else "Break Time",
            fontSize = 24.sp,
            color = if (isStudyTime) Color(0xFF4CAF50) else Color(0xFF2196F3)
        )

        Text(
            text = "${timeLeft / 60}:${String.format("%02d", timeLeft % 60)}",
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold
        )

        // Task selection button
        OutlinedButton(onClick = { showTaskDialog = true }) {
            Text(selectedTask?.title ?: "Select Task")
        }

        // Task selection dialog
        if (showTaskDialog) {
            AlertDialog(
                onDismissRequest = { showTaskDialog = false },
                title = { Text("Select Task") },
                text = {
                    Column {
                        incompleteTasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTask = task
                                        timeLeft = 25 * 60 // Default to 25 minutes
                                        showTaskDialog = false
                                    }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(task.title)
                                IconButton(onClick = {
                                    val updatedTask = task.copy(isCompleted = true) // Mark task as completed
                                    viewModel.updateTask(updatedTask)
                                }) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Complete Task")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTaskDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Control buttons (Start/Pause and Reset)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilledTonalButton(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Clear else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Start"
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isRunning) "Pause" else "Start")
            }

            OutlinedButton(onClick = {
                isRunning = false
                timeLeft = userMinutes.toInt() * 60 + userSeconds.toInt()
            }) {
                Icon(Icons.Default.Refresh, "Reset")
                Spacer(Modifier.width(8.dp))
                Text("Reset")
            }
        }
        if (selectedTask!=null){
        Button(
            onClick = {
                // Create a new task with the updated 'isCompleted' status
                selectedTask?.let {
                    val updatedTask = it.copy(isCompleted = true)
                    // Update the task in the database
                    viewModel.updateTask(updatedTask)
                }
                selectedTask?.title ="Select Task"
                userMinutes="00"
                userSeconds="00"
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Mark Completed")
        }

}
        // Time setter input fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = userMinutes,
                onValueChange = { userMinutes = it.filter { char -> char.isDigit() }.take(2) },
                label = { Text("Minutes") },
                modifier = Modifier.width(80.dp)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = userSeconds,
                onValueChange = { userSeconds = it.filter { char -> char.isDigit() }.take(2) },
                label = { Text("Seconds") },
                modifier = Modifier.width(80.dp)
            )
        }
    }
}
