package com.example.planstudy.ui.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.planstudy.data.TaskDao
import com.example.planstudy.data.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    val tasks = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var showDeleteToast by mutableStateOf(false)
        internal set

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
            showDeleteToast = true
        }
    }
}

class TaskViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
