package com.example.examprep

import android.content.Context
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.examprep.data.CategoryCatalog
import com.example.examprep.data.QuestionAssetRepository
import com.example.examprep.data.UserFirestoreRepository
import com.example.examprep.data.authStateFlow
import com.example.examprep.data.signOutUser
import com.example.examprep.model.AppThemeMode
import com.example.examprep.model.ExamCategory
import com.example.examprep.model.ExamResultSummary
import com.example.examprep.model.MockTestSetMeta
import com.example.examprep.model.UiState
import com.example.examprep.ui.ExamPrepViewModel
import com.example.examprep.ui.screens.AuthScreen
import com.example.examprep.ui.screens.CategoryDetailScreen
import com.example.examprep.ui.screens.ExamResultScreen
import com.example.examprep.ui.screens.ExamScreen
import com.example.examprep.ui.screens.HomeScreen
import com.example.examprep.ui.screens.PracticeSetsScreen
import com.example.examprep.ui.screens.ProfileScreen
import com.example.examprep.ui.screens.PyqScreen
import com.example.examprep.ui.screens.SectionalScreen
import com.example.examprep.ui.theme.ExamPrepTheme
import com.examprep.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

private enum class AppScreen {
    HOME,
    PRACTICE,
    SECTIONAL,
    PYQ,
    CATEGORY_DETAIL,
    EXAM,
    EXAM_RESULT,
    PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = AndroidColor.TRANSPARENT,
                darkScrim = AndroidColor.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = AndroidColor.TRANSPARENT,
                darkScrim = AndroidColor.TRANSPARENT
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            AppRoot()
        }
    }
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    val auth = Firebase.auth
    val currentUser by auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = auth.currentUser)

    val initialLocalTheme = remember {
        val prefs = context.getSharedPreferences(ExamPrepViewModel.PREFS_NAME, Context.MODE_PRIVATE)
        AppThemeMode.fromString(prefs.getString(ExamPrepViewModel.KEY_THEME_MODE, AppThemeMode.PAPER.name))
    }

    val user = currentUser
    if (user == null) {
        ExamPrepTheme(themeMode = initialLocalTheme) {
            AuthScreen(
                onAuthSuccess = {
                    // AuthStateListener updates currentUser reactively
                }
            )
        }
    } else {
        key(user.uid) {
            AuthenticatedAppContent(user = user)
        }
    }
}

@Composable
private fun AuthenticatedAppContent(user: FirebaseUser) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: ExamPrepViewModel = viewModel(
        key = user.uid,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                val prefs = app.getSharedPreferences(ExamPrepViewModel.PREFS_NAME, Context.MODE_PRIVATE)
                ExamPrepViewModel(
                    userRepository = UserFirestoreRepository(db),
                    questionAssetRepository = QuestionAssetRepository(app.assets),
                    prefs = prefs,
                    currentUserId = user.uid,
                    displayName = user.displayName?.takeIf { it.isNotBlank() } ?: "Student",
                    email = user.email
                )
            }
        }
    )

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val examAttemptsState by viewModel.examAttempts.collectAsStateWithLifecycle()
    val setsByCategory by viewModel.setsByCategory.collectAsStateWithLifecycle()
    val activeExamState by viewModel.activeExamState.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var previousSectionScreen by remember { mutableStateOf(AppScreen.PRACTICE) }
    var selectedCategory by remember { mutableStateOf<ExamCategory?>(null) }
    var selectedSetMeta by remember { mutableStateOf<MockTestSetMeta?>(null) }
    var examAttemptKey by remember { mutableIntStateOf(0) }
    var lastExamResult by remember { mutableStateOf<ExamResultSummary?>(null) }

    val showBottomNav = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.PRACTICE,
        AppScreen.SECTIONAL,
        AppScreen.PYQ,
        AppScreen.PROFILE
    )

    ExamPrepTheme(themeMode = themeMode) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.HOME,
                            onClick = { currentScreen = AppScreen.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            modifier = Modifier.testTag("nav_home")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.PRACTICE,
                            onClick = { currentScreen = AppScreen.PRACTICE },
                            icon = { Icon(Icons.Default.Assignment, contentDescription = "Practice") },
                            label = { Text("Practice") },
                            modifier = Modifier.testTag("nav_practice")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.SECTIONAL,
                            onClick = { currentScreen = AppScreen.SECTIONAL },
                            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Sectional") },
                            label = { Text("Sectional") },
                            modifier = Modifier.testTag("nav_sectional")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.PYQ,
                            onClick = { currentScreen = AppScreen.PYQ },
                            icon = { Icon(Icons.Default.HistoryEdu, contentDescription = "PYQ") },
                            label = { Text("PYQ") },
                            modifier = Modifier.testTag("nav_pyq")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.PROFILE,
                            onClick = { currentScreen = AppScreen.PROFILE },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            modifier = Modifier.testTag("nav_profile")
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            studentName = user.displayName?.takeIf { it.isNotBlank() } ?: "Student",
                            themeMode = themeMode,
                            examAttemptsState = examAttemptsState,
                            onThemeModeSelected = { viewModel.setThemeMode(it) },
                            onNavigateToPractice = { currentScreen = AppScreen.PRACTICE },
                            onNavigateToSectional = { currentScreen = AppScreen.SECTIONAL },
                            onNavigateToPyq = { currentScreen = AppScreen.PYQ }
                        )
                    }

                    AppScreen.PRACTICE -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        PracticeSetsScreen(
                            categories = CategoryCatalog.practiceMockCategories,
                            setCountsByCategory = setsByCategory.mapValues { it.value.size },
                            onBackClick = { currentScreen = AppScreen.HOME },
                            onCategoryClick = { category ->
                                selectedCategory = category
                                previousSectionScreen = AppScreen.PRACTICE
                                currentScreen = AppScreen.CATEGORY_DETAIL
                            }
                        )
                    }

                    AppScreen.SECTIONAL -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        SectionalScreen(
                            categories = CategoryCatalog.sectionalCategories,
                            onBackClick = { currentScreen = AppScreen.HOME },
                            onCategoryClick = { category ->
                                selectedCategory = category
                                previousSectionScreen = AppScreen.SECTIONAL
                                currentScreen = AppScreen.CATEGORY_DETAIL
                            }
                        )
                    }

                    AppScreen.PYQ -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        PyqScreen(
                            categories = CategoryCatalog.pyqCategories,
                            setCountsByCategory = setsByCategory.mapValues { it.value.size },
                            onBackClick = { currentScreen = AppScreen.HOME },
                            onCategoryClick = { category ->
                                selectedCategory = category
                                previousSectionScreen = AppScreen.PYQ
                                currentScreen = AppScreen.CATEGORY_DETAIL
                            }
                        )
                    }

                    AppScreen.CATEGORY_DETAIL -> {
                        val category = selectedCategory
                        if (category == null) {
                            currentScreen = previousSectionScreen
                        } else {
                            BackHandler { currentScreen = previousSectionScreen }
                            val sets = setsByCategory[category.id].orEmpty()
                            CategoryDetailScreen(
                                category = category,
                                sets = sets,
                                onBackClick = { currentScreen = previousSectionScreen },
                                onStartSet = { meta ->
                                    selectedSetMeta = meta
                                    examAttemptKey += 1
                                    viewModel.loadExamSet(meta)
                                    currentScreen = AppScreen.EXAM
                                }
                            )
                        }
                    }

                    AppScreen.EXAM -> {
                        when (val examState = activeExamState) {
                            null, is UiState.Loading -> {
                                BackHandler {
                                    viewModel.clearActiveExam()
                                    currentScreen = AppScreen.CATEGORY_DETAIL
                                }
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Loading question paper...",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            is UiState.Error -> {
                                BackHandler {
                                    viewModel.clearActiveExam()
                                    currentScreen = AppScreen.CATEGORY_DETAIL
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = examState.message,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = {
                                                viewModel.clearActiveExam()
                                                currentScreen = AppScreen.CATEGORY_DETAIL
                                            }
                                        ) {
                                            Text("Back to Sets")
                                        }
                                    }
                                }
                            }

                            is UiState.Success -> {
                                key(examAttemptKey, examState.data.meta.id) {
                                    ExamScreen(
                                        mockTestSet = examState.data,
                                        onExitExam = {
                                            viewModel.clearActiveExam()
                                            currentScreen = AppScreen.CATEGORY_DETAIL
                                        },
                                        onFinishExam = { resultSummary ->
                                            lastExamResult = resultSummary
                                            viewModel.recordPracticeExamAttempt(resultSummary)
                                            currentScreen = AppScreen.EXAM_RESULT
                                        }
                                    )
                                }
                            }
                        }
                    }

                    AppScreen.EXAM_RESULT -> {
                        val result = lastExamResult
                        if (result == null) {
                            currentScreen = AppScreen.CATEGORY_DETAIL
                        } else {
                            BackHandler {
                                viewModel.clearActiveExam()
                                currentScreen = AppScreen.CATEGORY_DETAIL
                            }
                            ExamResultScreen(
                                result = result,
                                onRetakeExam = {
                                    val meta = selectedSetMeta ?: result.set.meta
                                    examAttemptKey += 1
                                    viewModel.loadExamSet(meta)
                                    currentScreen = AppScreen.EXAM
                                },
                                onDoneClick = {
                                    viewModel.clearActiveExam()
                                    currentScreen = AppScreen.CATEGORY_DETAIL
                                }
                            )
                        }
                    }

                    AppScreen.PROFILE -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        ProfileScreen(
                            displayName = user.displayName?.takeIf { it.isNotBlank() } ?: "Student",
                            email = user.email,
                            themeMode = themeMode,
                            examAttemptsState = examAttemptsState,
                            onThemeModeSelected = { viewModel.setThemeMode(it) },
                            onSignOutClick = {
                                signOutUser(
                                    credentialManager = credentialManager,
                                    scope = scope
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
