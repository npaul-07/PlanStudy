package com.example.planstudy.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planstudy.data.AppDatabase
import com.example.planstudy.data.model.Task
import com.example.planstudy.ui.tasks.TaskViewModel
import com.example.planstudy.ui.tasks.TaskViewModelFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(taskDao = AppDatabase.getDatabase(context).taskDao()))
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Study Planner",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TaskCalendar(
            tasks = tasks,
            onDateSelected = { date ->
                selectedDate = date
            },
//            onTaskClick = onTaskClick,
            initialSelectedDate = selectedDate
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskCalendar(
    tasks: List<Task>,
    onDateSelected: (LocalDate) -> Unit = {},
    onTaskClick: (Task) -> Unit = {},
    initialSelectedDate: LocalDate = LocalDate.now()
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(initialSelectedDate)) }
    var selectedDate by remember { mutableStateOf(initialSelectedDate) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Month navigation
        CalendarHeader(
            currentMonth = currentMonth,
            onPreviousClick = { currentMonth = currentMonth.minusMonths(1) },
            onNextClick = { currentMonth = currentMonth.plusMonths(1) }
        )
        WeekdayHeader()

        // Calendar grid
        TaskCalendarGrid(
            tasks = tasks,
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            onDateClick = { date ->
                selectedDate = date
                onDateSelected(date)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TaskList(
            tasks = tasks.filter {
                it.deadline.toLocalDate() == selectedDate
            },
            onTaskClick = onTaskClick,
            selectedDate = selectedDate
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous Month"
            )
        }

        Text(
            text = currentMonth.format(formatter),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next Month"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekdayHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (day in DayOfWeek.values()) {
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskCalendarGrid(
    tasks: List<Task>,
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    val firstOfMonth = currentMonth.atDay(1)
    val daysBeforeMonth = firstOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    val dates = remember(currentMonth) {
        val result = mutableListOf<LocalDate?>()

        for (i in 0 until firstOfMonth.dayOfWeek.value % 7) {
            result.add(null)
        }
        for (i in 1..daysInMonth) {
            result.add(firstOfMonth.plusDays((i - 1).toLong()))
        }
        val remainingDays = 42 - result.size
        for (i in 0 until remainingDays) {
            result.add(null)
        }

        result
    }

    // Group tasks by date
    val tasksByDate = remember(tasks) {
        tasks.groupBy { it.deadline.toLocalDate() }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.height(300.dp)
    ) {
        items(dates) { date ->
            TaskDateCell(
                date = date,
                tasksForDate = date?.let { tasksByDate[it] } ?: emptyList(),
                isSelected = date == selectedDate,
                isCurrentMonth = date?.month == currentMonth.month,
                onDateClick = onDateClick
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskDateCell(
    date: LocalDate?,
    tasksForDate: List<Task>?,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    onDateClick: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .then(
                if (date != null) {
                    Modifier.clickable { onDateClick(date) }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            val isToday = date.equals(LocalDate.now())

            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (isToday && !isSelected) {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                } else {
                    null
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            !isCurrentMonth -> Color.Gray
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )

                    // Task indicators
                    if (!tasksForDate.isNullOrEmpty()) {
                        Row(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Show up to 3 task indicators
                            for (i in 0 until minOf(3, tasksForDate.size)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .padding(horizontal = 1.dp)
                                        .clip(CircleShape)
                                        .background(getPriorityColor(tasksForDate[i].priority))
                                )
                            }

                            if (tasksForDate.size > 3) {
                                Text(
                                    text = "+",
                                    fontSize = 10.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(start = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    selectedDate: LocalDate
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (tasks.isEmpty()) {
            Text(
                text = "No tasks for this date",
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                items(tasks.sortedBy { it.priority.ordinal }) { task ->
                    TaskItem(task = task, onClick = { onTaskClick(task) })
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = if (task.isCompleted) 0.6f else 1f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(getPriorityColor(task.priority))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Subject: ${task.subject}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Due: ${task.deadline.format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun getPriorityColor(priority: Task.Priority): Color {
    return when (priority) {
        Task.Priority.HIGH -> Color.Red
        Task.Priority.MEDIUM -> Color(0xFFFFA500) // Orange
        Task.Priority.LOW -> Color.Green
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toLocalDate(): LocalDate = LocalDate.of(year, month, dayOfMonth)