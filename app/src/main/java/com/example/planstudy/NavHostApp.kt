package com.example.studyplanner

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planstudy.ui.calendar.CalendarScreen
import com.example.planstudy.ui.screens.Books.BookRecommendationScreen
import com.example.planstudy.ui.screens.home.HomeScreen
import com.example.planstudy.ui.screens.pomodoro.PomodoroScreen
import com.example.planstudy.ui.screens.tasks.TaskScreen

// NavHostApp.kt
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home // Start directly with home
    ) {
        composable<Routes.Home> { HomeScreen(navController as Modifier) }
        composable<Routes.Tasks> { TaskScreen() }
        composable<Routes.Pomodoro> { PomodoroScreen(navController as Modifier) }
        composable<Routes.Books> { BookRecommendationScreen() }
        composable<Routes.Calendar> { CalendarScreen() }
    }
}
