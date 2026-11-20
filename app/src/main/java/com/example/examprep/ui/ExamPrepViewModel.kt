package com.example.examprep.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examprep.data.UserFirestoreRepository
import com.example.examprep.model.AppThemeMode
import com.example.examprep.model.ExamAttemptDoc
import com.example.examprep.model.UiState
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamPrepViewModel(
    private val userRepository: UserFirestoreRepository,
    private val prefs: SharedPreferences,
    private val currentUserId: String,
    private val displayName: String,
    private val email: String?
) : ViewModel() {

    private val _themeMode = MutableStateFlow(
        AppThemeMode.fromString(prefs.getString(KEY_THEME_MODE, AppThemeMode.PAPER.name))
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    val examAttempts: StateFlow<UiState<List<ExamAttemptDoc>>> = userRepository
        .observeExamAttempts(currentUserId)
        .map<List<ExamAttemptDoc>, UiState<List<ExamAttemptDoc>>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            Log.w(TAG, "Error observing exam attempts", rootError)
            emit(UiState.Error(rootError.message ?: "Unable to load exam history"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    init {
        initializeProfileAndTheme()
    }

    private fun initializeProfileAndTheme() {
        viewModelScope.launch {
            userRepository.ensureUserProfile(
                displayName = displayName,
                email = email,
                defaultTheme = _themeMode.value
            )
            userRepository.observeUserProfile(currentUserId)
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    Log.w(TAG, "Error observing user profile", firestoreError ?: error)
                }
                .collect { profile ->
                    if (profile != null) {
                        val remoteMode = AppThemeMode.fromString(profile.themeMode)
                        if (_themeMode.value != remoteMode) {
                            _themeMode.value = remoteMode
                            prefs.edit().putString(KEY_THEME_MODE, remoteMode.name).apply()
                        }
                    }
                }
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        viewModelScope.launch {
            userRepository.updateThemeMode(mode)
        }
    }

    companion object {
        private const val TAG = "ExamPrepVM"
        private const val STOP_TIMEOUT_MILLIS = 5000L
        const val PREFS_NAME = "examprep_prefs"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
