package com.example.examprep

import com.example.examprep.data.CategoryCatalog
import com.example.examprep.data.QuestionAssetRepository
import com.example.examprep.model.CategoryType
import com.example.examprep.model.ExamResultSummary
import com.example.examprep.model.McqQuestion
import com.example.examprep.model.MockTestSet
import com.example.examprep.model.MockTestSetMeta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExamPrepRepositoryTest {

    @Test
    fun testPracticeMockCategories() {
        val categories = CategoryCatalog.practiceMockCategories
        assertEquals(4, categories.size)
        assertEquals(
            listOf("Full Length", "GK", "Math and Reasoning", "Odia and English"),
            categories.map { it.name }
        )
        assertTrue(categories.all { it.sectionType == CategoryType.PRACTICE })
    }

    @Test
    fun testSectionalCategories() {
        val categories = CategoryCatalog.sectionalCategories
        assertEquals(10, categories.size)
        assertEquals(
            listOf(
                "Computer",
                "Economics",
                "English",
                "Geography",
                "History",
                "Math",
                "Odia",
                "Odisha GK",
                "Polity",
                "Reasoning"
            ),
            categories.map { it.name }
        )
        assertTrue(categories.all { it.sectionType == CategoryType.SECTIONAL })
    }

    @Test
    fun testPyqCategories() {
        val categories = CategoryCatalog.pyqCategories
        assertEquals(3, categories.size)
        assertEquals(
            listOf("OSSSC", "OSSC", "OPSC"),
            categories.map { it.name }
        )
        assertTrue(categories.all { it.sectionType == CategoryType.PYQ })
    }

    @Test
    fun testAllCategoryIdsAreUnique() {
        val allCategories = CategoryCatalog.practiceMockCategories +
            CategoryCatalog.sectionalCategories +
            CategoryCatalog.pyqCategories
        assertEquals(17, allCategories.size)
        assertEquals(allCategories.size, allCategories.map { it.id }.toSet().size)
    }

    @Test
    fun testBundledFullLengthAssetsExistAndAreNonEmpty() {
        val assetsRoot = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).firstOrNull { it.exists() }
            ?: error("Could not locate assets directory")

        val indexFile = File(assetsRoot, QuestionAssetRepository.FULL_LENGTH_INDEX_PATH)
        assertTrue("index.json must exist", indexFile.exists())
        val indexText = indexFile.readText(Charsets.UTF_8)

        val assetPathRegex = Regex("\"assetPath\"\\s*:\\s*\"([^\"]+)\"")
        val assetPaths = assetPathRegex.findAll(indexText).map { it.groupValues[1] }.toList()
        assertEquals("Expected 29 unique full-length PDF mock sets", 29, assetPaths.size)
        assertEquals("Asset paths must be unique", assetPaths.size, assetPaths.toSet().size)

        val imagePathRegex = Regex("\"imageAssetPath\"\\s*:\\s*\"([^\"]+)\"")
        var totalQuestionBlocks = 0
        for (relPath in assetPaths) {
            val setFile = File(assetsRoot, relPath)
            assertTrue("Missing set JSON: $relPath", setFile.exists())
            val content = setFile.readText(Charsets.UTF_8)
            assertTrue("Set JSON must not be empty: $relPath", content.length > 500)
            val qCount = Regex("\"correctIndex\"\\s*:\\s*[0-3]").findAll(content).count()
            assertTrue("Set $relPath must contain >= 90 valid MCQs, found $qCount", qCount >= 90)
            assertTrue("No invalid correctIndex (-1) allowed in $relPath", !content.contains("\"correctIndex\":-1"))
            totalQuestionBlocks += qCount

            for (imgMatch in imagePathRegex.findAll(content)) {
                val imgRel = imgMatch.groupValues[1]
                val imgFile = File(assetsRoot, imgRel)
                assertTrue("Missing diagram asset $imgRel referenced by $relPath", imgFile.exists() && imgFile.length() > 100)
            }
        }
        assertEquals(2889, totalQuestionBlocks)
    }

    @Test
    fun testBundledGkAssetsExistAndAreClean() {
        val assetsRoot = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).firstOrNull { it.exists() }
            ?: error("Could not locate assets directory")

        val indexFile = File(assetsRoot, QuestionAssetRepository.GK_INDEX_PATH)
        assertTrue("practice_gk/index.json must exist", indexFile.exists())
        val indexText = indexFile.readText(Charsets.UTF_8)

        val assetPathRegex = Regex("\"assetPath\"\\s*:\\s*\"([^\"]+)\"")
        val assetPaths = assetPathRegex.findAll(indexText).map { it.groupValues[1] }.toList()
        assertEquals("Expected 33 unique GK PDF mock sets", 33, assetPaths.size)
        assertEquals("GK asset paths must be unique", assetPaths.size, assetPaths.toSet().size)

        var totalGkQuestions = 0
        for (relPath in assetPaths) {
            val setFile = File(assetsRoot, relPath)
            assertTrue("Missing GK set JSON: $relPath", setFile.exists())
            val content = setFile.readText(Charsets.UTF_8)
            assertTrue("GK set JSON must not be empty: $relPath", content.length > 500)
            val qCount = Regex("\"correctIndex\"\\s*:\\s*[0-3]").findAll(content).count()
            assertTrue("GK set $relPath must contain >= 90 valid MCQs, found $qCount", qCount >= 90)
            assertTrue("No invalid correctIndex (-1) allowed in $relPath", !content.contains("\"correctIndex\":-1"))
            assertTrue("No inline Answer: leaks allowed in $relPath", !content.contains("Answer: ("))
            assertTrue("No draft self-correction leaks allowed in $relPath", !content.contains("Wait —"))
            totalGkQuestions += qCount
        }
        assertTrue("Expected > 3200 clean GK questions, found $totalGkQuestions", totalGkQuestions > 3200)
    }

    @Test
    fun testBundledMathReasoningAssetsExistAndAreClean() {
        val assetsRoot = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).firstOrNull { it.exists() }
            ?: error("Could not locate assets directory")

        val indexFile = File(assetsRoot, QuestionAssetRepository.MATH_REASONING_INDEX_PATH)
        assertTrue("practice_math_reasoning/index.json must exist", indexFile.exists())
        val indexText = indexFile.readText(Charsets.UTF_8)

        val assetPathRegex = Regex("\"assetPath\"\\s*:\\s*\"([^\"]+)\"")
        val assetPaths = assetPathRegex.findAll(indexText).map { it.groupValues[1] }.toList()
        assertEquals("Expected 46 unique Math & Reasoning PDF mock sets", 46, assetPaths.size)
        assertEquals("Math & Reasoning asset paths must be unique", assetPaths.size, assetPaths.toSet().size)

        val imagePathRegex = Regex("\"imageAssetPath\"\\s*:\\s*\"([^\"]+)\"")
        var totalQuestions = 0
        var totalDiagramRefs = 0
        for (relPath in assetPaths) {
            val setFile = File(assetsRoot, relPath)
            assertTrue("Missing Math & Reasoning set JSON: $relPath", setFile.exists())
            val content = setFile.readText(Charsets.UTF_8)
            assertTrue("Set JSON must not be empty: $relPath", content.length > 500)
            val qCount = Regex("\"correctIndex\"\\s*:\\s*[0-3]").findAll(content).count()
            assertEquals("Set $relPath must contain 100 valid MCQs", 100, qCount)
            assertTrue("No invalid correctIndex (-1) allowed in $relPath", !content.contains("\"correctIndex\":-1"))
            assertTrue("No inline Answer: leaks allowed in $relPath", !content.contains("Answer: ("))
            assertTrue("No draft Wait leaks allowed in $relPath", !content.contains("Wait:") && !content.contains("Wait —"))
            assertTrue("No replacement chars allowed in $relPath", !content.contains('\uFFFD'))
            assertTrue("No PUA chars allowed in $relPath", content.none { it in '\uE000'..'\uF8FF' })
            totalQuestions += qCount

            for (imgMatch in imagePathRegex.findAll(content)) {
                val imgRel = imgMatch.groupValues[1]
                val imgFile = File(assetsRoot, imgRel)
                assertTrue("Missing diagram asset $imgRel referenced by $relPath", imgFile.exists() && imgFile.length() > 100)
                totalDiagramRefs++
            }
        }
        assertEquals(4600, totalQuestions)
        assertTrue("Expected > 1000 diagram references across 46 Math & Reasoning sets, found $totalDiagramRefs", totalDiagramRefs > 1000)
    }

    @Test
    fun testBundledOdiaEnglishAssetsExistAndAreClean() {
        val assetsRoot = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).firstOrNull { it.exists() }
            ?: error("Could not locate assets directory")

        val indexFile = File(assetsRoot, QuestionAssetRepository.ODIA_ENGLISH_INDEX_PATH)
        assertTrue("practice_odia_english/index.json must exist", indexFile.exists())
        val indexText = indexFile.readText(Charsets.UTF_8)

        val assetPathRegex = Regex("\"assetPath\"\\s*:\\s*\"([^\"]+)\"")
        val assetPaths = assetPathRegex.findAll(indexText).map { it.groupValues[1] }.toList()
        assertEquals("Expected 29 unique Odia & English PDF mock sets", 29, assetPaths.size)
        assertEquals("Odia & English asset paths must be unique", assetPaths.size, assetPaths.toSet().size)

        var totalQuestions = 0
        var totalPassageRefs = 0
        val nonNullContextRegex = Regex("\"contextText\"\\s*:\\s*\"[^\"]+\"")
        for (relPath in assetPaths) {
            val setFile = File(assetsRoot, relPath)
            assertTrue("Missing Odia & English set JSON: $relPath", setFile.exists())
            val content = setFile.readText(Charsets.UTF_8)
            assertTrue("Set JSON must not be empty: $relPath", content.length > 500)
            val qCount = Regex("\"correctIndex\"\\s*:\\s*[0-3]").findAll(content).count()
            assertEquals("Set $relPath must contain 100 valid MCQs", 100, qCount)
            assertTrue("No invalid correctIndex (-1) allowed in $relPath", !content.contains("\"correctIndex\":-1"))
            assertTrue("No replacement chars allowed in $relPath", !content.contains('\uFFFD'))
            assertTrue("No PUA chars allowed in $relPath", content.none { it in '\uE000'..'\uF8FF' })
            assertTrue("No garbled OCR strings allowed in $relPath", !content.contains("GREENANS") && !content.contains("160 anea") && !content.contains("the penis"))
            assertTrue("Odia script must be present in $relPath", content.any { it in '\u0B00'..'\u0B7F' })
            totalQuestions += qCount
            totalPassageRefs += nonNullContextRegex.findAll(content).count()
        }
        assertEquals(2900, totalQuestions)
        assertEquals(250, totalPassageRefs)
    }

    @Test
    fun testBundledPyqOpscAssetsExistAndAreClean() {
        val assetsRoot = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).firstOrNull { it.exists() }
            ?: error("Could not locate assets directory")

        val indexFile = File(assetsRoot, QuestionAssetRepository.PYQ_OPSC_INDEX_PATH)
        assertTrue("pyq_opsc/index.json must exist", indexFile.exists())
        val indexText = indexFile.readText(Charsets.UTF_8)

        val assetPathRegex = Regex("\"assetPath\"\\s*:\\s*\"([^\"]+)\"")
        val assetPaths = assetPathRegex.findAll(indexText).map { it.groupValues[1] }.toList()
        assertEquals("Expected 30 OPSC PYQ sets", 30, assetPaths.size)
        assertEquals("OPSC PYQ asset paths must be unique", assetPaths.size, assetPaths.toSet().size)

        val imagePathRegex = Regex("\"imageAssetPath\"\\s*:\\s*\"([^\"]+)\"")
        var totalQuestions = 0
        var totalDiagramRefs = 0
        var hasOdiaScript = false
        for (relPath in assetPaths) {
            val setFile = File(assetsRoot, relPath)
            assertTrue("Missing OPSC PYQ set JSON: $relPath", setFile.exists())
            val content = setFile.readText(Charsets.UTF_8)
            assertTrue("OPSC PYQ set JSON must not be empty: $relPath", content.length > 500)
            val qCount = Regex("\"correctIndex\"\\s*:\\s*[0-3]").findAll(content).count()
            assertTrue("Set $relPath must contain >= 20 valid MCQs, found $qCount", qCount >= 20)
            assertTrue("No invalid correctIndex (-1) allowed in $relPath", !content.contains("\"correctIndex\":-1"))
            assertTrue("No replacement chars allowed in $relPath", !content.contains('\uFFFD'))
            assertTrue("No PUA chars allowed in $relPath", content.none { it in '\uE000'..'\uF8FF' })
            if (content.any { it in '\u0B00'..'\u0B7F' }) {
                hasOdiaScript = true
            }
            totalQuestions += qCount

            for (imgMatch in imagePathRegex.findAll(content)) {
                val imgRel = imgMatch.groupValues[1]
                val imgFile = File(assetsRoot, imgRel)
                assertTrue("Missing diagram asset $imgRel referenced by $relPath", imgFile.exists() && imgFile.length() > 100)
                totalDiagramRefs++
            }
        }
        assertEquals(2570, totalQuestions)
        assertTrue("Expected Odia script in OPSC PYQ sets", hasOdiaScript)
        assertTrue("Expected at least 79 diagram assets in OPSC PYQ sets, found $totalDiagramRefs", totalDiagramRefs >= 79)
    }

    @Test
    fun testExamResultSummaryScoringAndSectionBreakdown() {
        val meta = MockTestSetMeta(
            id = "mock_test_01",
            categoryId = QuestionAssetRepository.PRACTICE_FULL_LENGTH_CATEGORY_ID,
            title = "MOCK TEST - 1 (FULL LENGTH)",
            subtitle = "4 Questions",
            durationMinutes = 60,
            negativeMarking = 0.50,
            marksPerQuestion = 2.0,
            questionCount = 4,
            assetPath = "practice_full_length/mock_test_01.json"
        )
        val questions = listOf(
            McqQuestion(1, "Section I", null, null, "Q1?", listOf("A", "B", "C", "D"), 1),
            McqQuestion(2, "Section I", null, null, "Q2?", listOf("A", "B", "C", "D"), 0),
            McqQuestion(3, "Section II", null, null, "Q3?", listOf("A", "B", "C", "D"), 2),
            McqQuestion(4, "Section II", null, null, "Q4?", listOf("A", "B", "C", "D"), 3)
        )
        val result = ExamResultSummary(
            set = MockTestSet(meta, questions),
            userAnswers = mapOf(
                0 to 1, // Correct (+2.0)
                1 to 2, // Wrong (-0.5)
                2 to 2  // Correct (+2.0)
                // 3 skipped (0.0)
            ),
            timeTakenSeconds = 125
        )

        assertEquals(4, result.totalQuestions)
        assertEquals(2, result.correctCount)
        assertEquals(1, result.incorrectCount)
        assertEquals(1, result.unattemptedCount)
        assertEquals(3, result.attemptedCount)
        assertEquals(3.5, result.rawScore, 0.001)
        assertEquals(8.0, result.maxScore, 0.001)
        assertEquals(66, result.accuracyPercent)
        assertEquals(2, result.sectionBreakdown.size)
        assertEquals("Section I", result.sectionBreakdown[0].sectionName)
        assertEquals(1, result.sectionBreakdown[0].correct)
        assertEquals(1, result.sectionBreakdown[0].incorrect)
        assertEquals("Section II", result.sectionBreakdown[1].sectionName)
        assertEquals(1, result.sectionBreakdown[1].correct)
        assertEquals(1, result.sectionBreakdown[1].unattempted)
    }
}
