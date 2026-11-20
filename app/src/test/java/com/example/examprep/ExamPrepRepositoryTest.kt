package com.example.examprep

import com.example.examprep.data.CategoryCatalog
import com.example.examprep.model.CategoryType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
