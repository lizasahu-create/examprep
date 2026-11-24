package com.example.examprep.data

import android.content.Context
import com.examprep.R
import com.example.examprep.model.AppThemeMode
import com.example.examprep.model.CategoryType
import com.example.examprep.model.ExamAttemptDoc
import com.example.examprep.model.SetProgressDoc
import com.example.examprep.model.UserProfileDoc
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserFirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        val uid = auth.currentUser?.uid
        check(!uid.isNullOrBlank()) { "User must be signed in with Google before accessing Firestore." }
        return uid
    }

    private fun sanitizeDocId(rawId: String): String {
        val cleaned = rawId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128)
        return cleaned.ifBlank { "set_${System.currentTimeMillis()}" }
    }

    fun observeUserProfile(userId: String): Flow<UserProfileDoc?> = flow {
        if (userId.isBlank()) {
            emit(null)
            return@flow
        }
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfileDoc::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else null
                }
                .catch { error ->
                    val fsError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsError == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == userId) {
                        handleFirestoreError(fsError ?: error, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun ensureUserProfile(
        displayName: String,
        email: String?,
        defaultTheme: AppThemeMode
    ): Result<Unit> {
        var path = "users/unauthenticated"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            path = docRef.path
            val snapshot = docRef.get().await()
            val safeName = displayName.trim().ifBlank { "Student" }.take(100)
            val safeEmail = email?.trim()?.take(200)?.takeIf { it.isNotBlank() }

            if (!snapshot.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "email" to safeEmail,
                    "themeMode" to defaultTheme.name,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
            } else {
                val updatePayload = mutableMapOf<String, Any>(
                    "displayName" to safeName,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (safeEmail != null) {
                    updatePayload["email"] = safeEmail
                }
                docRef.update(updatePayload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun updateThemeMode(themeMode: AppThemeMode): Result<Unit> {
        var path = "users/unauthenticated"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            path = docRef.path
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                docRef.update(
                    mapOf(
                        "themeMode" to themeMode.name,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val displayName = auth.currentUser?.displayName?.trim()?.ifBlank { "Student" }?.take(100) ?: "Student"
                val email = auth.currentUser?.email?.trim()?.take(200)?.takeIf { it.isNotBlank() }
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to displayName,
                    "email" to email,
                    "themeMode" to themeMode.name,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    fun observeExamAttempts(userId: String): Flow<List<ExamAttemptDoc>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/exam_attempts"
        emitAll(
            db.collection("users").document(userId).collection("exam_attempts")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ExamAttemptDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val fsError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsError == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == userId) {
                        handleFirestoreError(fsError ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun saveExamAttempt(
        setId: String,
        setHeading: String,
        category: CategoryType,
        totalQuestions: Int,
        correctCount: Int,
        incorrectCount: Int,
        skippedCount: Int,
        timeTakenSeconds: Int
    ): Result<Unit> {
        var path = "users/unauthenticated/exam_attempts"
        return try {
            val uid = requireUserId()
            val safeTotal = totalQuestions.coerceIn(1, 1000)
            val safeCorrect = correctCount.coerceIn(0, safeTotal)
            val safeIncorrect = incorrectCount.coerceIn(0, safeTotal - safeCorrect)
            val safeSkipped = (safeTotal - safeCorrect - safeIncorrect).coerceAtLeast(0)
            val safeTime = timeTakenSeconds.coerceIn(0, 86400)
            val safeSetId = sanitizeDocId(setId)
            val safeHeading = setHeading.trim().ifBlank { "Practice Exam" }.take(200)

            val docRef = db.collection("users").document(uid).collection("exam_attempts").document()
            path = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "setId" to safeSetId,
                "setHeading" to safeHeading,
                "category" to category.name,
                "totalQuestions" to safeTotal,
                "correctCount" to safeCorrect,
                "incorrectCount" to safeIncorrect,
                "skippedCount" to safeSkipped,
                "timeTakenSeconds" to safeTime,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeSetProgress(userId: String): Flow<List<SetProgressDoc>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/set_progress"
        emitAll(
            db.collection("users").document(userId).collection("set_progress")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        SetProgressDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val fsError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsError == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == userId) {
                        handleFirestoreError(fsError ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun saveSetProgress(
        setId: String,
        setHeading: String,
        category: CategoryType,
        answeredCount: Int,
        correctCount: Int,
        totalQuestions: Int
    ): Result<Unit> {
        var path = "users/unauthenticated/set_progress"
        return try {
            val uid = requireUserId()
            val safeDocId = sanitizeDocId(setId)
            val safeTotal = totalQuestions.coerceIn(1, 1000)
            val safeAnswered = answeredCount.coerceIn(0, safeTotal)
            val safeCorrect = correctCount.coerceIn(0, safeAnswered)
            val safeHeading = setHeading.trim().ifBlank { "Question Set" }.take(200)
            val safeCategory = if (category == CategoryType.PRACTICE) CategoryType.OTHER else category

            val docRef = db.collection("users").document(uid)
                .collection("set_progress").document(safeDocId)
            path = docRef.path
            val existingSnap = docRef.get().await()
            if (!existingSnap.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "setHeading" to safeHeading,
                    "category" to safeCategory.name,
                    "answeredCount" to safeAnswered,
                    "correctCount" to safeCorrect,
                    "totalQuestions" to safeTotal,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                docRef.update(
                    mapOf(
                        "setHeading" to safeHeading,
                        "category" to safeCategory.name,
                        "answeredCount" to safeAnswered,
                        "correctCount" to safeCorrect,
                        "totalQuestions" to safeTotal,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }
}
