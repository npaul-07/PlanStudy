package com.example.planstudy.data

import androidx.room.*
import com.example.planstudy.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}