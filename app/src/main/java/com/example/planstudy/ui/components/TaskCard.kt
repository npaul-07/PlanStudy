package com.example.planstudy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TaskCard(
    title: String,
    deadline: String,
    priority: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = deadline)
            Spacer(modifier = Modifier.height(4.dp))

            // Using FilterChip instead of Chip
            FilterChip(
                selected = false,
                onClick = {},
                label = { Text(text = priority) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = when (priority) {
                        "HIGH" -> Color(0xFFFF5252) // A red shade for "High"
                        "MEDIUM" -> Color(0xFFFFC107) // A yellow shade for "Medium"
                        else -> Color(0xFF4CAF50) // A green shade for "Low"
                    },
                    labelColor = Color.Black
                )
            )

        }
    }
}