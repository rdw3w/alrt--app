package com.example

import com.example.data.db.DefaultData
import com.example.data.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun defaultRoutine_has23Tasks() {
        val routine = DefaultData.getDefaultRoutine()
        assertEquals(23, routine.size)
    }

    @Test
    fun subjectHoursTargets_exact13Hours() {
        val routine = DefaultData.getDefaultRoutine()
        val studyTasks = routine.filter { it.category == "STUDY" }

        val gkGsMins = studyTasks.filter { it.subjectKey == "GK_GS" }.sumOf { it.targetMinutes }
        val sciMins = studyTasks.filter { it.subjectKey == "SCIENCE" }.sumOf { it.targetMinutes }
        val arithMins = studyTasks.filter { it.subjectKey == "ARITHMETIC_MATHS" }.sumOf { it.targetMinutes }
        val advMins = studyTasks.filter { it.subjectKey == "ADVANCE_MATHS" }.sumOf { it.targetMinutes }
        val reasMins = studyTasks.filter { it.subjectKey == "REASONING" }.sumOf { it.targetMinutes }
        val hindiMins = studyTasks.filter { it.subjectKey == "HINDI" }.sumOf { it.targetMinutes }

        assertEquals("GK-GS must be 2 hours", 120, gkGsMins)
        assertEquals("Science must be 1 hour", 60, sciMins)
        assertEquals("Arithmetic Maths must be 3 hours", 180, arithMins)
        assertEquals("Advance Maths must be 3 hours", 180, advMins)
        assertEquals("Reasoning must be 2 hours", 120, reasMins)
        assertEquals("Hindi must be 2 hours", 120, hindiMins)

        val totalStudyMinutes = studyTasks.sumOf { it.targetMinutes }
        assertEquals("Total study time must be exactly 13 hours (780 minutes)", 780, totalStudyMinutes)
    }

    @Test
    fun defaultMorningChecklist_has9Items() {
        val morningList = DefaultData.getDefaultMorningChecklist()
        assertEquals(9, morningList.size)
    }

    @Test
    fun scienceCardsAndQuiz_areAvailable() {
        assertTrue(DefaultData.scienceCards.isNotEmpty())
        assertTrue(DefaultData.scienceQuiz.isNotEmpty())
    }
}

