package com.example.studyplanner

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

// routes.kt
@Serializable
sealed class Routes {
    @Serializable object Home
    @Serializable object Tasks
    @Serializable object Pomodoro
    @Serializable object Books
    @Serializable object Calendar
}