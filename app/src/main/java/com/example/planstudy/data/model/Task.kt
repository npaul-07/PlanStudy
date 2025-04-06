package com.example.planstudy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    val description: String,
    val deadline: LocalDateTime,
    val priority: Priority,
    val subject: String,
    val isCompleted: Boolean = false
) {
    enum class Priority {
        HIGH, MEDIUM, LOW
    }
}