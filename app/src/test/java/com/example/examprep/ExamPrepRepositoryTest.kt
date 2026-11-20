package com.example.examprep

import com.example.examprep.data.ExamPrepRepository
import org.junit.Assert.*
import org.junit.Test

class ExamPrepRepositoryTest {

    @Test
    fun testPracticeSetsInitialization() {
        val sets = ExamPrepRepository.practiceSets
        assertTrue("Practice sets should not be empty", sets.isNotEmpty())
        val gkSet = sets.find { it.category == "General Knowledge" }
        assertNotNull("GK set must exist", gkSet)
        assertTrue("GK set should have questions", gkSet!!.questions.isNotEmpty())
    }

    @Test
    fun testSectionalSubjectsCount() {
        val subjects = ExamPrepRepository.sectionalSubjects
        assertEquals("There must be 11 sectional subjects matching design mockup", 11, subjects.size)
        val names = subjects.map { it.name }
        assertTrue(names.contains("Mathematics"))
        assertTrue(names.contains("Odia"))
        assertTrue(names.contains("English"))
        assertTrue(names.contains("Reasoning"))
        assertTrue(names.contains("History"))
        assertTrue(names.contains("Geography"))
        assertTrue(names.contains("Economics"))
        assertTrue(names.contains("Polity"))
        assertTrue(names.contains("Science"))
        assertTrue(names.contains("Computer"))
        assertTrue(names.contains("Odisha GK"))
    }

    @Test
    fun testPyqPapersAvailable() {
        val papers = ExamPrepRepository.pyqPapers
        assertTrue("PYQ papers list should contain at least 5 verified papers", papers.size >= 5)
        val upsc = papers.find { it.examName.contains("UPSC") }
        assertNotNull("UPSC PYQ must be present", upsc)
        val opsc = papers.find { it.examName.contains("OPSC") }
        assertNotNull("OPSC PYQ must be present", opsc)
    }

    @Test
    fun testAdminAddQuestion() {
        val initialCount = ExamPrepRepository.adminMetrics.totalQuestions
        ExamPrepRepository.addNewQuestion(
            subject = "Science",
            question = "What is the powerhouse of the cell?",
            options = listOf("Nucleus", "Mitochondria", "Ribosome", "Chloroplast"),
            correctIndex = 1,
            explanation = "Mitochondria generate most of the chemical energy needed to power the cell's biochemical reactions."
        )
        assertEquals("Metric question count should increment", initialCount + 1, ExamPrepRepository.adminMetrics.totalQuestions)
        val latestLog = ExamPrepRepository.adminActivityLogs.first()
        assertTrue("Latest activity log should reflect added question", latestLog.activity.contains("Science"))
    }
}
