package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyProgress
import com.example.data.model.MorningChecklistItem
import com.example.data.model.RoutineTask
import com.example.data.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_tasks ORDER BY orderIndex ASC")
    fun getAllTasks(): Flow<List<RoutineTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: RoutineTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<RoutineTask>)

    @Update
    suspend fun updateTask(task: RoutineTask)

    @Delete
    suspend fun deleteTask(task: RoutineTask)

    @Query("DELETE FROM routine_tasks")
    suspend fun clearAll()

    @Query("UPDATE routine_tasks SET isCompleted = :completed, isSkipped = :skipped, isMissed = :missed WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, completed: Boolean, skipped: Boolean, missed: Boolean)

    @Query("UPDATE routine_tasks SET isCompleted = 0, isSkipped = 0, isMissed = 0")
    suspend fun resetAllStatus()
}

@Dao
interface MorningChecklistDao {
    @Query("SELECT * FROM morning_checklist ORDER BY orderIndex ASC")
    fun getAllChecklistItems(): Flow<List<MorningChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MorningChecklistItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<MorningChecklistItem>)

    @Update
    suspend fun updateItem(item: MorningChecklistItem)

    @Delete
    suspend fun deleteItem(item: MorningChecklistItem)

    @Query("UPDATE morning_checklist SET isChecked = :checked WHERE id = :id")
    suspend fun setItemChecked(id: Long, checked: Boolean)

    @Query("UPDATE morning_checklist SET isChecked = 0")
    suspend fun resetAll()

    @Query("DELETE FROM morning_checklist")
    suspend fun clearAll()
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE date = :date ORDER BY timestamp DESC")
    fun getSessionsForDate(date: String): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("DELETE FROM study_sessions")
    suspend fun clearAll()
}

@Dao
interface DailyProgressDao {
    @Query("SELECT * FROM daily_progress WHERE date = :date LIMIT 1")
    fun getProgressForDate(date: String): Flow<DailyProgress?>

    @Query("SELECT * FROM daily_progress ORDER BY date DESC")
    fun getAllProgress(): Flow<List<DailyProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: DailyProgress)

    @Query("UPDATE daily_progress SET waterGlasses = :glasses WHERE date = :date")
    suspend fun updateWater(date: String, glasses: Int)
}
