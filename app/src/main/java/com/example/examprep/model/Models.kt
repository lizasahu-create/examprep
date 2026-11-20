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
