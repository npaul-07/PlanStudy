package com.example.planstudy.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.planstudy.data.model.Task
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.deadline.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")),
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text(task.priority.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = when (task.priority) {
                            Task.Priority.HIGH -> Color(0xFFF44336) // Red for HIGH priority
                            Task.Priority.MEDIUM -> Color(0xFFFF9800) // Orange for MEDIUM priority
                            Task.Priority.LOW -> Color(0xFF4CAF50) // Green for LOW priority
                        }
                    )
                )

            }

            // Checkbox with visual feedback
            IconToggleButton(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() }
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF4CAF50) // Green color for completed tasks
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Mark Complete",
                        tint = Color(0xFFBDBDBD) // Gray color for incomplete tasks
                    )
                }
            }


            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}