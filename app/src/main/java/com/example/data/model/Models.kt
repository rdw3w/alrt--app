package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_tasks")
data class RoutineTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titleHi: String,
    val titleEn: String,
    val startTime: String, // HH:mm in 24h
    val endTime: String,   // HH:mm in 24h
    val category: String,  // STUDY, HEALTH, HYGIENE, MEALS, BREAKS, SLEEP, ROUTINE
    val subjectKey: String? = null, // GK_GS, SCIENCE, ARITHMETIC_MATHS, ADVANCE_MATHS, REASONING, HINDI
    val targetMinutes: Int = 0,
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false,
    val isSkipped: Boolean = false,
    val isMissed: Boolean = false,
    val notes: String = "",
    val customDay: String = "ALL" // ALL, WEEKDAYS, SUNDAY
)

@Entity(tableName = "morning_checklist")
data class MorningChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val textHi: String,
    val textEn: String,
    val isChecked: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectKey: String,
    val durationMinutes: Int,
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val topicCovered: String = "",
    val questionsSolved: Int = 0,
    val notes: String = ""
)

@Entity(tableName = "daily_progress")
data class DailyProgress(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val waterGlasses: Int = 0,
    val totalStudyMinutes: Int = 0,
    val completedTasksCount: Int = 0,
    val totalTasksCount: Int = 0,
    val notes: String = ""
)

enum class Subject(
    val key: String,
    val nameHi: String,
    val nameEn: String,
    val targetHours: Float,
    val targetMinutes: Int,
    val colorHex: Long
) {
    GK_GS("GK_GS", "सामान्य ज्ञान (GK-GS)", "General Knowledge (GK-GS)", 2.0f, 120, 0xFF4CC9F0),
    SCIENCE("SCIENCE", "सामान्य विज्ञान", "General Science", 1.0f, 60, 0xFF06D6A0),
    ARITHMETIC_MATHS("ARITHMETIC_MATHS", "अंकगणित (Arithmetic Maths)", "Arithmetic Maths", 3.0f, 180, 0xFFFFD166),
    ADVANCE_MATHS("ADVANCE_MATHS", "एडवांस मैथ्स (Advance Maths)", "Advance Maths", 3.0f, 180, 0xFFF77F00),
    REASONING("REASONING", "तर्कशक्ति (Reasoning)", "Reasoning Ability", 2.0f, 120, 0xFFB5179E),
    HINDI("HINDI", "सामान्य हिन्दी", "General Hindi", 2.0f, 120, 0xFF7209B7);

    companion object {
        fun fromKey(key: String?): Subject? = entries.find { it.key == key }
    }
}

enum class TaskCategory(
    val key: String,
    val labelHi: String,
    val labelEn: String,
    val colorHex: Long
) {
    STUDY("STUDY", "अध्ययन", "Study", 0xFF00F0FF),
    HEALTH("HEALTH", "स्वास्थ्य व व्यायाम", "Health & Exercise", 0xFF06D6A0),
    HYGIENE("HYGIENE", "स्वच्छता व नित्यक्रम", "Hygiene & Freshen Up", 0xFF48CAE4),
    MEALS("MEALS", "आहार व भोजन", "Meals & Nutrition", 0xFFFFD166),
    BREAKS("BREAKS", "विश्राम व ब्रेक", "Breaks & Rest", 0xFFA0AEC0),
    SLEEP("SLEEP", "निद्रा", "Sleep", 0xFF7209B7),
    ROUTINE("ROUTINE", "दिनचर्या योजना", "Routine & Planning", 0xFFE0AAFF);

    companion object {
        fun fromKey(key: String): TaskCategory = entries.find { it.key == key } ?: ROUTINE
    }
}

data class ScienceCard(
    val id: String,
    val category: String, // PHYSICS, CHEMISTRY, BIOLOGY, SCI_TECH
    val titleHi: String,
    val titleEn: String,
    val formulaOrFact: String,
    val explanationHi: String,
    val explanationEn: String,
    val examImportance: String // High, Very High
)

data class ScienceQuizQuestion(
    val id: Int,
    val questionHi: String,
    val questionEn: String,
    val optionsHi: List<String>,
    val optionsEn: List<String>,
    val correctIndex: Int,
    val explanationHi: String,
    val explanationEn: String
)
