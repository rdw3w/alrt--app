package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.DefaultData
import com.example.data.db.RudraDatabase
import com.example.data.model.DailyProgress
import com.example.data.model.MorningChecklistItem
import com.example.data.model.RoutineTask
import com.example.data.model.ScienceCard
import com.example.data.model.ScienceQuizQuestion
import com.example.data.model.StudySession
import com.example.data.model.Subject
import com.example.data.repository.RudraRepository
import com.example.notification.RudraNotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class TimerMode {
    COUNTDOWN,
    POMODORO,
    STOPWATCH
}

enum class TimerState {
    IDLE,
    RUNNING,
    PAUSED,
    FINISHED
}

data class TimerUiState(
    val mode: TimerMode = TimerMode.POMODORO,
    val state: TimerState = TimerState.IDLE,
    val selectedSubject: Subject = Subject.GK_GS,
    val totalSeconds: Int = 50 * 60, // 50 mins focus
    val remainingSeconds: Int = 50 * 60,
    val elapsedSeconds: Int = 0,
    val isBreakPhase: Boolean = false,
    val breakDurationSeconds: Int = 10 * 60,
    val sessionNotes: String = "",
    val questionsSolved: Int = 0
)

class RudraViewModel(application: Application) : AndroidViewModel(application) {

    private val database = RudraDatabase.getDatabase(application, viewModelScope)
    val repository = RudraRepository(
        routineDao = database.routineDao(),
        morningChecklistDao = database.morningChecklistDao(),
        studySessionDao = database.studySessionDao(),
        dailyProgressDao = database.dailyProgressDao()
    )

    val notificationHelper = RudraNotificationHelper(application)

    // Current IST Time State
    private val _istTimeFormatted = MutableStateFlow("")
    val istTimeFormatted: StateFlow<String> = _istTimeFormatted.asStateFlow()

    private val _istDateFormatted = MutableStateFlow("")
    val istDateFormatted: StateFlow<String> = _istDateFormatted.asStateFlow()

    private val _currentMinutesOfDay = MutableStateFlow(0)
    val currentMinutesOfDay: StateFlow<Int> = _currentMinutesOfDay.asStateFlow()

    // Tasks list
    val allTasks: StateFlow<List<RoutineTask>> = repository.allTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Morning checklist
    val morningChecklist: StateFlow<List<MorningChecklistItem>> = repository.checklistItems.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Study sessions
    val studySessions: StateFlow<List<StudySession>> = repository.allSessions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Settings
    private val _isHindi = MutableStateFlow(true)
    val isHindi: StateFlow<Boolean> = _isHindi.asStateFlow()

    private val _is24HourFormat = MutableStateFlow(false)
    val is24HourFormat: StateFlow<Boolean> = _is24HourFormat.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _preAlertsEnabled = MutableStateFlow(true)
    val preAlertsEnabled: StateFlow<Boolean> = _preAlertsEnabled.asStateFlow()

    private val _remindersScheduledCount = MutableStateFlow(0)
    val remindersScheduledCount: StateFlow<Int> = _remindersScheduledCount.asStateFlow()

    private val _waterGlassesToday = MutableStateFlow(4)
    val waterGlassesToday: StateFlow<Int> = _waterGlassesToday.asStateFlow()

    // Focus Timer state
    private val _timerState = MutableStateFlow(TimerUiState())
    val timerState: StateFlow<TimerUiState> = _timerState.asStateFlow()

    private var timerJob: Job? = null

    // Science Cards & Quiz
    val scienceCards: List<ScienceCard> = DefaultData.scienceCards
    val scienceQuiz: List<ScienceQuizQuestion> = DefaultData.scienceQuiz

    private val _quizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quizAnswers: StateFlow<Map<Int, Int>> = _quizAnswers.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
            allTasks.collect { taskList ->
                if (taskList.isNotEmpty()) {
                    notificationHelper.scheduleAllRoutineReminders(
                        taskList,
                        isHindi = _isHindi.value,
                        preAlerts = _preAlertsEnabled.value
                    )
                    _remindersScheduledCount.value = taskList.size
                }
            }
        }
        startClockTicker()
    }

    fun scheduleAllRemindersNow() {
        viewModelScope.launch {
            val list = allTasks.value
            if (list.isNotEmpty()) {
                notificationHelper.scheduleAllRoutineReminders(
                    list,
                    isHindi = _isHindi.value,
                    preAlerts = _preAlertsEnabled.value
                )
                notificationHelper.scheduleNextWaterReminder(60, _isHindi.value)
                _remindersScheduledCount.value = list.size
            }
        }
    }

    fun togglePreAlerts() {
        _preAlertsEnabled.value = !_preAlertsEnabled.value
        scheduleAllRemindersNow()
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            val istZone = TimeZone.getTimeZone("Asia/Kolkata")
            val time12Format = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH).apply { timeZone = istZone }
            val time24Format = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).apply { timeZone = istZone }
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH).apply { timeZone = istZone }

            while (true) {
                val now = Date()
                val cal = Calendar.getInstance(istZone).apply { time = now }
                val minutesOfDay = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
                _currentMinutesOfDay.value = minutesOfDay

                val timeStr = if (_is24HourFormat.value) {
                    time24Format.format(now)
                } else {
                    time12Format.format(now)
                }
                _istTimeFormatted.value = timeStr
                _istDateFormatted.value = dateFormat.format(now)

                delay(1000)
            }
        }
    }

    fun toggleLanguage() {
        _isHindi.value = !_isHindi.value
    }

    fun toggleTimeFormat() {
        _is24HourFormat.value = !_is24HourFormat.value
    }

    fun toggleSound() {
        _soundEnabled.value = !_soundEnabled.value
    }

    fun toggleVibration() {
        _vibrationEnabled.value = !_vibrationEnabled.value
    }

    fun incrementWater() {
        _waterGlassesToday.value = (_waterGlassesToday.value + 1).coerceAtMost(20)
        saveDailyWater()
    }

    fun decrementWater() {
        _waterGlassesToday.value = (_waterGlassesToday.value - 1).coerceAtLeast(0)
        saveDailyWater()
    }

    private fun saveDailyWater() {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.updateWater(todayStr, _waterGlassesToday.value)
        }
    }

    fun markTaskDone(taskId: Long) {
        viewModelScope.launch {
            repository.markTaskStatus(taskId, completed = true, skipped = false, missed = false)
        }
    }

    fun markTaskSkipped(taskId: Long) {
        viewModelScope.launch {
            repository.markTaskStatus(taskId, completed = false, skipped = true, missed = false)
        }
    }

    fun markTaskMissed(taskId: Long) {
        viewModelScope.launch {
            repository.markTaskStatus(taskId, completed = false, skipped = false, missed = true)
        }
    }

    fun resetTaskStatus(taskId: Long) {
        viewModelScope.launch {
            repository.markTaskStatus(taskId, completed = false, skipped = false, missed = false)
        }
    }

    fun restoreDefaultSchedule() {
        viewModelScope.launch {
            repository.restoreDefaultRoutine()
        }
    }

    fun saveTask(task: RoutineTask) {
        viewModelScope.launch {
            if (task.id == 0L) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task)
            }
        }
    }

    fun deleteTask(task: RoutineTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleChecklistItem(id: Long, isChecked: Boolean) {
        viewModelScope.launch {
            repository.toggleChecklistItem(id, isChecked)
        }
    }

    fun addChecklistItem(item: MorningChecklistItem) {
        viewModelScope.launch {
            repository.addChecklistItem(item)
        }
    }

    fun deleteChecklistItem(item: MorningChecklistItem) {
        viewModelScope.launch {
            repository.deleteChecklistItem(item)
        }
    }

    fun resetMorningChecklist() {
        viewModelScope.launch {
            repository.resetMorningChecklist()
        }
    }

    fun restoreDefaultChecklist() {
        viewModelScope.launch {
            repository.restoreDefaultChecklist()
        }
    }

    // Timer controls
    fun setTimerMode(mode: TimerMode) {
        timerJob?.cancel()
        val duration = when (mode) {
            TimerMode.POMODORO -> 50 * 60
            TimerMode.COUNTDOWN -> 60 * 60
            TimerMode.STOPWATCH -> 0
        }
        _timerState.value = _timerState.value.copy(
            mode = mode,
            state = TimerState.IDLE,
            totalSeconds = duration,
            remainingSeconds = duration,
            elapsedSeconds = 0,
            isBreakPhase = false
        )
    }

    fun setSelectedSubject(subject: Subject) {
        _timerState.value = _timerState.value.copy(selectedSubject = subject)
    }

    fun setTimerDurationMinutes(minutes: Int) {
        if (_timerState.value.state == TimerState.RUNNING) return
        val sec = minutes * 60
        _timerState.value = _timerState.value.copy(
            totalSeconds = sec,
            remainingSeconds = sec
        )
    }

    fun startTimer() {
        if (_timerState.value.state == TimerState.RUNNING) return
        _timerState.value = _timerState.value.copy(state = TimerState.RUNNING)

        timerJob = viewModelScope.launch {
            while (_timerState.value.state == TimerState.RUNNING) {
                delay(1000)
                val current = _timerState.value
                if (current.mode == TimerMode.STOPWATCH) {
                    _timerState.value = current.copy(
                        elapsedSeconds = current.elapsedSeconds + 1
                    )
                } else {
                    if (current.remainingSeconds > 1) {
                        _timerState.value = current.copy(
                            remainingSeconds = current.remainingSeconds - 1,
                            elapsedSeconds = current.elapsedSeconds + 1
                        )
                    } else {
                        // Finished
                        if (current.mode == TimerMode.POMODORO && !current.isBreakPhase) {
                            // Switch to break phase
                            _timerState.value = current.copy(
                                isBreakPhase = true,
                                remainingSeconds = current.breakDurationSeconds,
                                totalSeconds = current.breakDurationSeconds
                            )
                            notificationHelper.showNotification(
                                8888,
                                "पॉमोडोरो ब्रेक का समय (Break Time)",
                                "बधाई! 50 मिनट का अध्ययन पूरा हुआ। अब 10 मिनट का रिफ्रेशिंग ब्रेक लें।"
                            )
                        } else {
                            _timerState.value = current.copy(
                                state = TimerState.FINISHED,
                                remainingSeconds = 0
                            )
                            notificationHelper.showNotification(
                                8889,
                                "फोकस सत्र समाप्त (Focus Session Complete)",
                                "आपका अध्ययन सत्र सफलतापूर्वक पूर्ण हुआ! अपनी प्रगति दर्ज करें।"
                            )
                            break
                        }
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(state = TimerState.PAUSED)
    }

    fun resumeTimer() {
        startTimer()
    }

    fun resetTimer() {
        timerJob?.cancel()
        val defaultSec = when (_timerState.value.mode) {
            TimerMode.POMODORO -> 50 * 60
            TimerMode.COUNTDOWN -> 60 * 60
            TimerMode.STOPWATCH -> 0
        }
        _timerState.value = _timerState.value.copy(
            state = TimerState.IDLE,
            remainingSeconds = defaultSec,
            totalSeconds = defaultSec,
            elapsedSeconds = 0,
            isBreakPhase = false
        )
    }

    fun finishAndLogSession(topic: String, questionsCount: Int, notes: String) {
        val elapsed = _timerState.value.elapsedSeconds
        val minutes = (elapsed / 60).coerceAtLeast(1)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            repository.logStudySession(
                StudySession(
                    subjectKey = _timerState.value.selectedSubject.key,
                    durationMinutes = minutes,
                    date = todayStr,
                    topicCovered = topic,
                    questionsSolved = questionsCount,
                    notes = notes
                )
            )
            resetTimer()
        }
    }

    fun answerQuiz(questionId: Int, selectedOption: Int) {
        val updated = _quizAnswers.value.toMutableMap()
        updated[questionId] = selectedOption
        _quizAnswers.value = updated
    }

    fun sendTestAlert() {
        notificationHelper.sendTestNotification(_isHindi.value)
    }

    // Helper to compute minutes from "HH:mm"
    fun timeToMinutes(timeStr: String): Int {
        val parts = timeStr.trim().split(":")
        if (parts.size >= 2) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].toIntOrNull() ?: 0
            return h * 60 + m
        }
        return 0
    }

    fun formatMinutesToTime(totalMin: Int, is24h: Boolean): String {
        var normalized = totalMin % (24 * 60)
        if (normalized < 0) normalized += 24 * 60
        val h = normalized / 60
        val m = normalized % 60
        return if (is24h) {
            String.format(Locale.getDefault(), "%02d:%02d", h, m)
        } else {
            val ampm = if (h >= 12) "PM" else "AM"
            val displayH = if (h == 0) 12 else if (h > 12) h - 12 else h
            String.format(Locale.getDefault(), "%02d:%02d %s", displayH, m, ampm)
        }
    }

    fun formatTimeString(timeStr: String, is24h: Boolean): String {
        return formatMinutesToTime(timeToMinutes(timeStr), is24h)
    }
}
