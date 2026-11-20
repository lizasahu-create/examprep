package com.example.examprep.model

data class Question(
    val id: String,
    val subject: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class PracticeSet(
    val id: String,
    val title: String,
    val category: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val questions: List<Question>
)

data class SectionalSubject(
    val id: String,
    val name: String,
    val pagesCount: Int,
    val description: String,
    val topics: List<String>,
    val studyNotes: List<StudyNote>
)

data class StudyNote(
    val id: String,
    val title: String,
    val content: String,
    val formulasOrHighlights: List<String>,
    val practiceQuestion: String? = null,
    val practiceAnswer: String? = null
)

data class PreviousYearPaper(
    val id: String,
    val examName: String,
    val year: Int,
    val pagesCount: Int,
    val paperType: String,
    val overview: String,
    val questionsSnapshot: List<String>
)

data class UserProgress(
    val studentName: String = "Student",
    val email: String = "student@example.com",
    val totalTests: Int = 5,
    val completedTests: Int = 2,
    val accuracyPercent: Int = 85,
    val streakDays: Int = 4,
    val bookmarkedIds: Set<String> = emptySet()
)

data class AdminMetric(
    val totalQuestions: Int = 1250,
    val practiceSets: Int = 12,
    val sectionalPdfs: Int = 11,
    val pyqFiles: Int = 15
)

data class AdminActivityLog(
    val id: String,
    val date: String,
    val activity: String,
    val user: String
)
