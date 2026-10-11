package com.example.data.repository

import com.example.data.db.DailyProgressDao
import com.example.data.db.DefaultData
import com.example.data.db.MorningChecklistDao
import com.example.data.db.RoutineDao
import com.example.data.db.StudySessionDao
import com.example.data.model.DailyProgress
import com.example.data.model.MorningChecklistItem
import com.example.data.model.RoutineTask
import com.example.data.model.StudySession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class RudraRepository(
    private val routineDao: RoutineDao,
    private val morningChecklistDao: MorningChecklistDao,
    private val studySessionDao: StudySessionDao,
    private val dailyProgressDao: DailyProgressDao
) {
    val allTasks: Flow<List<RoutineTask>> = routineDao.getAllTasks()
    val checklistItems: Flow<List<MorningChecklistItem>> = morningChecklistDao.getAllChecklistItems()
    val allSessions: Flow<List<StudySession>> = studySessionDao.getAllSessions()

    fun getSessionsForDate(date: String): Flow<List<StudySession>> =
        studySessionDao.getSessionsForDate(date)

    fun getDailyProgress(date: String): Flow<DailyProgress?> =
        dailyProgressDao.getProgressForDate(date)

    val allDailyProgress: Flow<List<DailyProgress>> =
        dailyProgressDao.getAllProgress()

    suspend fun insertTask(task: RoutineTask): Long = routineDao.insertTask(task)

    suspend fun updateTask(task: RoutineTask) = routineDao.updateTask(task)

    suspend fun deleteTask(task: RoutineTask) = routineDao.deleteTask(task)

    suspend fun markTaskStatus(id: Long, completed: Boolean, skipped: Boolean, missed: Boolean) {
        routineDao.updateTaskStatus(id, completed, skipped, missed)
    }

    suspend fun restoreDefaultRoutine() {
        routineDao.clearAll()
        routineDao.insertTasks(DefaultData.getDefaultRoutine())
    }

    suspend fun toggleChecklistItem(id: Long, isChecked: Boolean) {
        morningChecklistDao.setItemChecked(id, isChecked)
    }

    suspend fun addChecklistItem(item: MorningChecklistItem) {
        morningChecklistDao.insertItem(item)
    }

    suspend fun deleteChecklistItem(item: MorningChecklistItem) {
        morningChecklistDao.deleteItem(item)
    }

    suspend fun resetMorningChecklist() {
        morningChecklistDao.resetAll()
    }

    suspend fun restoreDefaultChecklist() {
        morningChecklistDao.clearAll()
        morningChecklistDao.insertItems(DefaultData.getDefaultMorningChecklist())
    }

    suspend fun logStudySession(session: StudySession) {
        studySessionDao.insertSession(session)
    }

    suspend fun deleteStudySession(id: Long) {
        studySessionDao.deleteSession(id)
    }

    suspend fun updateWater(date: String, glasses: Int) {
        val current = dailyProgressDao.getProgressForDate(date).first()
        val updated = current?.copy(waterGlasses = glasses) ?: DailyProgress(
            date = date,
            waterGlasses = glasses
        )
        dailyProgressDao.upsertProgress(updated)
    }

    suspend fun checkAndSeedInitialData() {
        val existingTasks = routineDao.getAllTasks().first()
        if (existingTasks.isEmpty()) {
            routineDao.insertTasks(DefaultData.getDefaultRoutine())
        }
        val existingChecklist = morningChecklistDao.getAllChecklistItems().first()
        if (existingChecklist.isEmpty()) {
            morningChecklistDao.insertItems(DefaultData.getDefaultMorningChecklist())
        }
    }

    suspend fun exportDataAsJson(): String {
        val tasks = routineDao.getAllTasks().first()
        val checklist = morningChecklistDao.getAllChecklistItems().first()
        val sessions = studySessionDao.getAllSessions().first()
        val progressList = dailyProgressDao.getAllProgress().first()

        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "RUDRA STUDY LIFE PRO")

        val tasksArray = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("titleHi", task.titleHi)
            obj.put("titleEn", task.titleEn)
            obj.put("startTime", task.startTime)
            obj.put("endTime", task.endTime)
            obj.put("category", task.category)
            obj.put("subjectKey", task.subjectKey ?: "")
            obj.put("targetMinutes", task.targetMinutes)
            obj.put("orderIndex", task.orderIndex)
            obj.put("isCompleted", task.isCompleted)
            obj.put("isSkipped", task.isSkipped)
            obj.put("isMissed", task.isMissed)
            obj.put("notes", task.notes)
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        val sessionsArray = JSONArray()
        sessions.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("subjectKey", s.subjectKey)
            obj.put("durationMinutes", s.durationMinutes)
            obj.put("date", s.date)
            obj.put("timestamp", s.timestamp)
            obj.put("topicCovered", s.topicCovered)
            obj.put("questionsSolved", s.questionsSolved)
            obj.put("notes", s.notes)
            sessionsArray.put(obj)
        }
        root.put("sessions", sessionsArray)

        return root.toString(2)
    }

    suspend fun importDataFromJson(jsonStr: String): Boolean {
        return try {
            val root = JSONObject(jsonStr)
            if (root.has("tasks")) {
                val tasksArray = root.getJSONArray("tasks")
                val tasksList = mutableListOf<RoutineTask>()
                for (i in 0 until tasksArray.length()) {
                    val obj = tasksArray.getJSONObject(i)
                    tasksList.add(
                        RoutineTask(
                            id = obj.optLong("id", 0),
                            titleHi = obj.optString("titleHi", ""),
                            titleEn = obj.optString("titleEn", ""),
                            startTime = obj.optString("startTime", "05:00"),
                            endTime = obj.optString("endTime", "05:10"),
                            category = obj.optString("category", "ROUTINE"),
                            subjectKey = obj.optString("subjectKey").takeIf { it.isNotBlank() },
                            targetMinutes = obj.optInt("targetMinutes", 0),
                            orderIndex = obj.optInt("orderIndex", i + 1),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            isSkipped = obj.optBoolean("isSkipped", false),
                            isMissed = obj.optBoolean("isMissed", false),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
                if (tasksList.isNotEmpty()) {
                    routineDao.clearAll()
                    routineDao.insertTasks(tasksList)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
