package com.example.planstudy.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.planstudy.data.AppDatabase
import com.example.planstudy.ui.components.ProgressCard
import com.example.planstudy.ui.components.TaskCard
import com.example.planstudy.ui.tasks.TaskViewModel
import com.example.planstudy.ui.tasks.TaskViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planstudy.data.model.Task
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(taskDao = AppDatabase.getDatabase(context).taskDao()))
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    val currentTime = LocalDateTime.now()

    // Filter tasks: High priority and due within the next 2 days
    val highPriorityTasksDueInTwoDays = tasks.filter { task ->
        val deadline = task.deadline
        val isHighPriority = task.priority == Task.Priority.HIGH
        val isDueWithinTwoDays = Duration.between(currentTime, deadline).toDays() in 0..2

        isHighPriority && isDueWithinTwoDays && !task.isCompleted // Ensure task is incomplete
    }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Today's Plan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ProgressCard(
                progress = 0.65f,
                subject = "Mathematics",
                timeSpent = "2h 30m"
            )
        }
        fun formatDeadline(deadline: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
            val daysBetween = ChronoUnit.DAYS.between(currentTime.toLocalDate(), deadline.toLocalDate())

            return when {
                daysBetween == 0L -> "Due: Today, ${deadline.format(formatter)}"
                daysBetween == 1L -> "Due: Tomorrow, ${deadline.format(formatter)}"
                else -> "Due: ${deadline.format(DateTimeFormatter.ofPattern("MMM dd, yyyy, h:mm a"))}"
            }
        }
        // Display high priority tasks that are due within the next 2 days
        highPriorityTasksDueInTwoDays.forEach { task ->
            item {
                TaskCard(
                    title = task.title,
                    deadline = formatDeadline(task.deadline),
                    priority = task.priority.name
                )
            }
        }

    }
}
