package com.example.examprep.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.data.CategoryCatalog
import com.example.examprep.model.AppThemeMode
import com.example.examprep.model.ExamAttemptDoc
import com.example.examprep.model.UiState
import com.example.examprep.ui.components.PaperCard

@Composable
fun HomeScreen(
    studentName: String,
    themeMode: AppThemeMode,
    examAttemptsState: UiState<List<ExamAttemptDoc>>,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToSectional: () -> Unit,
    onNavigateToPyq: () -> Unit
) {
    val practiceCount = CategoryCatalog.practiceMockCategories.size
    val sectionalCount = CategoryCatalog.sectionalCategories.size
    val pyqCount = CategoryCatalog.pyqCategories.size

    val attempts = (examAttemptsState as? UiState.Success)?.data.orEmpty()
    val totalTestsTaken = attempts.size
    val totalCorrect = attempts.sumOf { it.correctCount }
    val totalQuestionsAttempted = attempts.sumOf { it.totalQuestions }
    val accuracyPercent = if (totalQuestionsAttempted > 0L) {
        ((totalCorrect * 100L) / totalQuestionsAttempted).toInt()
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("home_screen")
    ) {
        // Top Header with Brand
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "ExamPrep Logo",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "ExamPrep",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Greeting
        Text(
            text = "Hello, $studentName!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Paper Mode / Dark Mode Toggle right on Home Page
        ThemeModeSelectorBar(
            selectedMode = themeMode,
            onModeSelected = onThemeModeSelected
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Core Category Sections
        Text(
            text = "Categories",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeCategoryCard(
            title = "Practice Mock",
            subtitle = "Full Length, GK, Math & Reasoning, Odia & English",
            icon = Icons.Default.Timer,
            iconTint = MaterialTheme.colorScheme.primary,
            iconBg = MaterialTheme.colorScheme.primaryContainer,
            badge = "$practiceCount Categories",
            testTag = "home_card_practice",
            onClick = onNavigateToPractice
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeCategoryCard(
            title = "Sectional",
            subtitle = "Computer, Economics, English, Geography, History & more",
            icon = Icons.Default.MenuBook,
            iconTint = MaterialTheme.colorScheme.secondary,
            iconBg = MaterialTheme.colorScheme.secondaryContainer,
            badge = "$sectionalCount Categories",
            testTag = "home_card_sectional",
            onClick = onNavigateToSectional
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeCategoryCard(
            title = "PYQ",
            subtitle = "OSSSC, OSSC, OPSC",
            icon = Icons.Default.HistoryEdu,
            iconTint = MaterialTheme.colorScheme.tertiary,
            iconBg = MaterialTheme.colorScheme.tertiaryContainer,
            badge = "$pyqCount Categories",
            testTag = "home_card_pyq",
            onClick = onNavigateToPyq
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Exam Performance Summary from Firestore
        Text(
            text = "Your Performance",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                modifier = Modifier.weight(1f),
                title = "Exams Taken",
                value = "$totalTestsTaken",
                accentColor = MaterialTheme.colorScheme.primary
            )
            StatBox(
                modifier = Modifier.weight(1f),
                title = "Correct",
                value = "$totalCorrect",
                accentColor = MaterialTheme.colorScheme.tertiary
            )
            StatBox(
                modifier = Modifier.weight(1f),
                title = "Accuracy",
                value = "$accuracyPercent%",
                accentColor = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ThemeModeSelectorBar(
    selectedMode: AppThemeMode,
    onModeSelected: (AppThemeMode) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("theme_mode_selector"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ThemeModeChip(
                label = "Paper Mode",
                icon = Icons.Default.WbSunny,
                selected = selectedMode == AppThemeMode.PAPER,
                modifier = Modifier
                    .weight(1f)
                    .testTag("paper_mode_button"),
                onClick = { onModeSelected(AppThemeMode.PAPER) }
            )
            ThemeModeChip(
                label = "Dark Mode",
                icon = Icons.Default.DarkMode,
                selected = selectedMode == AppThemeMode.DARK,
                modifier = Modifier
                    .weight(1f)
                    .testTag("dark_mode_button"),
                onClick = { onModeSelected(AppThemeMode.DARK) }
            )
        }
    }
}

@Composable
private fun ThemeModeChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
fun HomeCategoryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    badge: String,
    testTag: String,
    onClick: () -> Unit
) {
    PaperCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        elevation = 2.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(iconBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        color = iconTint,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open $title",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color
) {
    PaperCard(
        modifier = modifier,
        elevation = 1.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
