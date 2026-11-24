package com.example.examprep.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class AppThemeMode {
    PAPER,
    DARK;

    companion object {
        fun fromString(raw: String?): AppThemeMode =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: PAPER
    }
}

enum class CategoryType(val displayTitle: String) {
    PRACTICE("Practice Mock"),
    SECTIONAL("Sectional"),
    PYQ("PYQ"),
    OTHER("Other")
}

data class ExamCategory(
    val id: String,
    val name: String,
    val sectionType: CategoryType
)

// --- Bundled MCQ Question & Mock Set Models ---

data class McqQuestion(
    val number: Int,
    val section: String = "",
    val contextText: String? = null,
    val imageAssetPath: String? = null,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int
)

data class MockTestSetMeta(
    val id: String,
    val categoryId: String,
    val title: String,
    val subtitle: String,
    val durationMinutes: Int,
    val negativeMarking: Double,
    val marksPerQuestion: Double,
    val questionCount: Int,
    val assetPath: String
)

data class MockTestSet(
    val meta: MockTestSetMeta,
    val questions: List<McqQuestion>
)

data class SectionScoreSummary(
    val sectionName: String,
    val total: Int,
    val correct: Int,
    val incorrect: Int,
    val unattempted: Int
)

data class ExamResultSummary(
    val set: MockTestSet,
    val userAnswers: Map<Int, Int>, // questionIndex -> selectedOptionIndex (0..3)
    val timeTakenSeconds: Int
) {
    val totalQuestions: Int = set.questions.size

    val correctCount: Int = set.questions.indices.count { idx ->
        userAnswers[idx] == set.questions[idx].correctIndex
    }

    val incorrectCount: Int = set.questions.indices.count { idx ->
        val selected = userAnswers[idx]
        selected != null && selected != set.questions[idx].correctIndex
    }

    val unattemptedCount: Int = totalQuestions - correctCount - incorrectCount

    val attemptedCount: Int = correctCount + incorrectCount

    val maxScore: Double = totalQuestions * set.meta.marksPerQuestion

    val rawScore: Double =
        (correctCount * set.meta.marksPerQuestion) - (incorrectCount * set.meta.negativeMarking)

    val accuracyPercent: Int = if (attemptedCount > 0) {
        ((correctCount * 100.0) / attemptedCount).toInt()
    } else 0

    val sectionBreakdown: List<SectionScoreSummary> = set.questions
        .mapIndexed { idx, q -> idx to q }
        .groupBy { (_, q) -> q.section.ifBlank { "General" } }
        .map { (secName, pairs) ->
            var c = 0
            var w = 0
            var u = 0
            for ((idx, q) in pairs) {
                val sel = userAnswers[idx]
                when {
                    sel == null -> u++
                    sel == q.correctIndex -> c++
                    else -> w++
                }
            }
            SectionScoreSummary(
                sectionName = secName,
                total = pairs.size,
                correct = c,
                incorrect = w,
                unattempted = u
            )
        }
}

// --- Firestore Models (Type-safe, @Keep, @IgnoreExtraProperties, no-arg defaults) ---

@Keep
@IgnoreExtraProperties
data class UserProfileDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String? = null,
    val themeMode: String = AppThemeMode.PAPER.name,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ExamAttemptDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val setId: String = "",
    val setHeading: String = "",
    val category: String = CategoryType.PRACTICE.name,
    val totalQuestions: Long = 0L,
    val correctCount: Long = 0L,
    val incorrectCount: Long = 0L,
    val skippedCount: Long = 0L,
    val timeTakenSeconds: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SetProgressDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val setHeading: String = "",
    val category: String = CategoryType.SECTIONAL.name,
    val answeredCount: Long = 0L,
    val correctCount: Long = 0L,
    val totalQuestions: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
