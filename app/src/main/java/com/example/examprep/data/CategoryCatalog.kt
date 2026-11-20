package com.example.examprep.data

import com.example.examprep.model.CategoryType
import com.example.examprep.model.ExamCategory

object CategoryCatalog {

    val practiceMockCategories: List<ExamCategory> = listOf(
        ExamCategory(
            id = "practice_full_length",
            name = "Full Length",
            sectionType = CategoryType.PRACTICE
        ),
        ExamCategory(
            id = "practice_gk",
            name = "GK",
            sectionType = CategoryType.PRACTICE
        ),
        ExamCategory(
            id = "practice_math_reasoning",
            name = "Math and Reasoning",
            sectionType = CategoryType.PRACTICE
        ),
        ExamCategory(
            id = "practice_odia_english",
            name = "Odia and English",
            sectionType = CategoryType.PRACTICE
        )
    )

    val sectionalCategories: List<ExamCategory> = listOf(
        ExamCategory(
            id = "sectional_computer",
            name = "Computer",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_economics",
            name = "Economics",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_english",
            name = "English",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_geography",
            name = "Geography",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_history",
            name = "History",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_math",
            name = "Math",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_odia",
            name = "Odia",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_odisha_gk",
            name = "Odisha GK",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_polity",
            name = "Polity",
            sectionType = CategoryType.SECTIONAL
        ),
        ExamCategory(
            id = "sectional_reasoning",
            name = "Reasoning",
            sectionType = CategoryType.SECTIONAL
        )
    )

    val pyqCategories: List<ExamCategory> = listOf(
        ExamCategory(
            id = "pyq_osssc",
            name = "OSSSC",
            sectionType = CategoryType.PYQ
        ),
        ExamCategory(
            id = "pyq_ossc",
            name = "OSSC",
            sectionType = CategoryType.PYQ
        ),
        ExamCategory(
            id = "pyq_opsc",
            name = "OPSC",
            sectionType = CategoryType.PYQ
        )
    )
}
