package com.example.planstudy

import android.app.Application
import com.example.planstudy.data.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StudyPlannerApp : Application() {
    // Initialize database with application context
    val database by lazy { AppDatabase.getDatabase(this) }
}