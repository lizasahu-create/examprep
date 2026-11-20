package com.example.examprep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.examprep.data.ExamPrepRepository
import com.example.examprep.model.PracticeSet
import com.example.examprep.model.PreviousYearPaper
import com.example.examprep.model.SectionalSubject
import com.example.examprep.model.UserProgress
import com.example.examprep.ui.screens.*
import com.example.examprep.ui.theme.ExamPrepTheme
import com.example.examprep.ui.theme.PaperBackground
import com.example.examprep.ui.theme.PrimaryNavy

enum class Screen {
    SPLASH,
    LOGIN,
    HOME,
    PRACTICE_SETS,
    EXAM,
    EXAM_RESULT,
    SECTIONAL,
    DOCUMENT_READER,
    PYQ,
    ADMIN,
    PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamPrepTheme {
                var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
                var activePracticeSet by remember { mutableStateOf<PracticeSet?>(null) }
                var lastUserAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
                var lastTimeTakenSeconds by remember { mutableIntStateOf(0) }
                var activeSectionalSubject by remember { mutableStateOf<SectionalSubject?>(null) }
                var activePyqPaper by remember { mutableStateOf<PreviousYearPaper?>(null) }
                var readerTitle by remember { mutableStateOf("") }
                var readerPages by remember { mutableIntStateOf(20) }

                var isAdmin by remember { mutableStateOf(false) }
                var userProgress by remember {
                    mutableStateOf(
                        UserProgress(
                            studentName = "Rohit Sharma",
                            email = "student@example.com",
                            totalTests = 5,
                            completedTests = 2,
                            accuracyPercent = 85,
                            streakDays = 4
                        )
                    )
                }

                val showBottomNav = currentScreen in listOf(
                    Screen.HOME,
                    Screen.PRACTICE_SETS,
                    Screen.SECTIONAL,
                    Screen.PYQ,
                    Screen.PROFILE
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar(
                                containerColor = Color.White,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == Screen.HOME,
                                    onClick = { currentScreen = Screen.HOME },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PrimaryNavy,
                                        selectedTextColor = PrimaryNavy,
                                        indicatorColor = Color(0xFFE8EEF8)
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.PRACTICE_SETS,
                                    onClick = { currentScreen = Screen.PRACTICE_SETS },
                                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Practice") },
                                    label = { Text("Practice") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PrimaryNavy,
                                        selectedTextColor = PrimaryNavy,
                                        indicatorColor = Color(0xFFE8EEF8)
                                    ),
                                    modifier = Modifier.testTag("nav_practice")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.SECTIONAL,
                                    onClick = { currentScreen = Screen.SECTIONAL },
                                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Sectional") },
                                    label = { Text("Sectional") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PrimaryNavy,
                                        selectedTextColor = PrimaryNavy,
                                        indicatorColor = Color(0xFFE8EEF8)
                                    ),
                                    modifier = Modifier.testTag("nav_sectional")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.PYQ,
                                    onClick = { currentScreen = Screen.PYQ },
                                    icon = { Icon(Icons.Default.HistoryEdu, contentDescription = "PYQ") },
                                    label = { Text("PYQ") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PrimaryNavy,
                                        selectedTextColor = PrimaryNavy,
                                        indicatorColor = Color(0xFFE8EEF8)
                                    ),
                                    modifier = Modifier.testTag("nav_pyq")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.PROFILE,
                                    onClick = { currentScreen = Screen.PROFILE },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text("Profile") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PrimaryNavy,
                                        selectedTextColor = PrimaryNavy,
                                        indicatorColor = Color(0xFFE8EEF8)
                                    ),
                                    modifier = Modifier.testTag("nav_profile")
                                )
                            }
                        }
                    },
                    containerColor = PaperBackground
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.SPLASH -> {
                                SplashScreen(
                                    onStartClick = { currentScreen = Screen.LOGIN }
                                )
                            }

                            Screen.LOGIN -> {
                                LoginScreen(
                                    onLoginSuccess = { isAdm, email ->
                                        isAdmin = isAdm
                                        userProgress = userProgress.copy(
                                            email = email,
                                            studentName = if (isAdm) "Admin" else "Rohit Sharma"
                                        )
                                        currentScreen = if (isAdm) Screen.ADMIN else Screen.HOME
                                    },
                                    onSkip = { currentScreen = Screen.HOME }
                                )
                            }

                            Screen.HOME -> {
                                HomeScreen(
                                    userProgress = userProgress,
                                    onNavigateToPractice = { currentScreen = Screen.PRACTICE_SETS },
                                    onNavigateToSectional = { currentScreen = Screen.SECTIONAL },
                                    onNavigateToPyq = { currentScreen = Screen.PYQ },
                                    onNavigateToAdmin = { currentScreen = Screen.ADMIN },
                                    isAdminUser = isAdmin
                                )
                            }

                            Screen.PRACTICE_SETS -> {
                                BackHandler { currentScreen = Screen.HOME }
                                PracticeSetsScreen(
                                    sets = ExamPrepRepository.practiceSets,
                                    onBackClick = { currentScreen = Screen.HOME },
                                    onStartSet = { set ->
                                        activePracticeSet = set
                                        currentScreen = Screen.EXAM
                                    }
                                )
                            }

                            Screen.EXAM -> {
                                val set = activePracticeSet ?: ExamPrepRepository.practiceSets.first()
                                BackHandler { currentScreen = Screen.PRACTICE_SETS }
                                ExamScreen(
                                    practiceSet = set,
                                    onBackClick = { currentScreen = Screen.PRACTICE_SETS },
                                    onSubmitExam = { answers, timeTaken ->
                                        lastUserAnswers = answers
                                        lastTimeTakenSeconds = timeTaken
                                        // Update stats
                                        userProgress = userProgress.copy(
                                            completedTests = userProgress.completedTests + 1
                                        )
                                        currentScreen = Screen.EXAM_RESULT
                                    }
                                )
                            }

                            Screen.EXAM_RESULT -> {
                                val set = activePracticeSet ?: ExamPrepRepository.practiceSets.first()
                                BackHandler { currentScreen = Screen.PRACTICE_SETS }
                                ExamResultScreen(
                                    practiceSet = set,
                                    userAnswers = lastUserAnswers,
                                    timeTakenSeconds = lastTimeTakenSeconds,
                                    onRetakeTest = { currentScreen = Screen.EXAM },
                                    onDone = { currentScreen = Screen.PRACTICE_SETS }
                                )
                            }

                            Screen.SECTIONAL -> {
                                BackHandler { currentScreen = Screen.HOME }
                                SectionalScreen(
                                    subjects = ExamPrepRepository.sectionalSubjects,
                                    onBackClick = { currentScreen = Screen.HOME },
                                    onSubjectClick = { subject ->
                                        activeSectionalSubject = subject
                                        readerTitle = "${subject.name} (Sectional)"
                                        readerPages = subject.pagesCount
                                        currentScreen = Screen.DOCUMENT_READER
                                    }
                                )
                            }

                            Screen.DOCUMENT_READER -> {
                                BackHandler { currentScreen = Screen.SECTIONAL }
                                DocumentReaderScreen(
                                    title = readerTitle,
                                    totalPages = readerPages,
                                    subjectData = activeSectionalSubject,
                                    onBackClick = { currentScreen = Screen.SECTIONAL }
                                )
                            }

                            Screen.PYQ -> {
                                BackHandler { currentScreen = Screen.HOME }
                                PyqScreen(
                                    papers = ExamPrepRepository.pyqPapers,
                                    onBackClick = { currentScreen = Screen.HOME },
                                    onOpenPaper = { paper ->
                                        activePyqPaper = paper
                                        readerTitle = "${paper.examName} ${paper.year} (PYQ)"
                                        readerPages = paper.pagesCount
                                        activeSectionalSubject = null
                                        currentScreen = Screen.DOCUMENT_READER
                                    }
                                )
                            }

                            Screen.ADMIN -> {
                                BackHandler { currentScreen = Screen.HOME }
                                AdminDashboardScreen(
                                    onBackClick = { currentScreen = Screen.HOME }
                                )
                            }

                            Screen.PROFILE -> {
                                BackHandler { currentScreen = Screen.HOME }
                                ProfileScreen(
                                    userProgress = userProgress,
                                    isAdminUser = isAdmin,
                                    onAdminToggle = { isAdmin = it },
                                    onNavigateToAdmin = { currentScreen = Screen.ADMIN },
                                    onLogout = { currentScreen = Screen.LOGIN }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
