package com.example.examprep.ui.screens

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.model.UserProgress
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.theme.*

@Composable
fun HomeScreen(
    userProgress: UserProgress,
    onNavigateToPractice: () -> Unit,
    onNavigateToSectional: () -> Unit,
    onNavigateToPyq: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    isAdminUser: Boolean
) {
    var showNotificationDialog by remember { mutableStateOf(false) }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Text(
                    text = "ExamPrep Notifications",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            },
            text = {
                Column {
                    Text("🔔 Daily GK Mock Test is live! Test your prep.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📄 New OPSC Prelims 2022 notes & answer keys uploaded.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⭐ 4-Day Study Streak maintained! Keep it up.", fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text("Close", color = PrimaryNavy)
                }
            },
            containerColor = PaperCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("home_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PrimaryNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Logo",
                        tint = AccentOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ExamPrep",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            }

            IconButton(
                onClick = { showNotificationDialog = true },
                modifier = Modifier.testTag("notification_button")
            ) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = AccentOrange) {
                            Text("3", color = Color.White)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = PrimaryNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Greeting Section
        Column {
            Text(
                text = "Hello, ${userProgress.studentName}!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Keep going, your goals are close!",
                fontSize = 14.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Cards (Practice Set, Sectional Questions, PYQ Section)
        HomeActionCard(
            title = "Practice Set",
            subtitle = "Timed MCQ Exam",
            icon = Icons.Default.Assignment,
            iconTint = PrimaryNavy,
            iconBg = Color(0xFFE8EEF8),
            badge = "5 Sets Available",
            testTag = "home_card_practice",
            onClick = onNavigateToPractice
        )

        Spacer(modifier = Modifier.height(14.dp))

        HomeActionCard(
            title = "Sectional Questions",
            subtitle = "Subject-wise Question Sheet",
            icon = Icons.Default.MenuBook,
            iconTint = AccentOrange,
            iconBg = Color(0xFFFFF2E8),
            badge = "11 Subjects",
            testTag = "home_card_sectional",
            onClick = onNavigateToSectional
        )

        Spacer(modifier = Modifier.height(14.dp))

        HomeActionCard(
            title = "PYQ Section",
            subtitle = "Previous Year Questions",
            icon = Icons.Default.HistoryEdu,
            iconTint = SuccessGreen,
            iconBg = Color(0xFFEAF9F0),
            badge = "6 Papers",
            testTag = "home_card_pyq",
            onClick = onNavigateToPyq
        )

        if (isAdminUser) {
            Spacer(modifier = Modifier.height(14.dp))
            HomeActionCard(
                title = "Admin Dashboard",
                subtitle = "Manage Questions, Upload Sets & Stats",
                icon = Icons.Default.AdminPanelSettings,
                iconTint = Color(0xFF7C3AED),
                iconBg = Color(0xFFF3E8FF),
                badge = "Admin Demo",
                testTag = "home_card_admin",
                onClick = onNavigateToAdmin
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Stats Title
        Text(
            text = "Quick Stats",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Stats Row matching Mockup #3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                modifier = Modifier.weight(1f),
                title = "Total Tests",
                value = "${userProgress.totalTests}",
                accentColor = PrimaryNavy
            )
            StatBox(
                modifier = Modifier.weight(1f),
                title = "Completed",
                value = "${userProgress.completedTests}",
                accentColor = AccentOrange
            )
            StatBox(
                modifier = Modifier.weight(1f),
                title = "Accuracy",
                value = "${userProgress.accuracyPercent}%",
                accentColor = SuccessGreen
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Challenge Card
        PaperCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PaperCardAlt
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Revision Sprint 🎯",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "10 high-yield questions on General Knowledge & Indian Polity.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onNavigateToPractice,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Start", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HomeActionCard(
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
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }

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
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted
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
        backgroundColor = PaperCard,
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
                color = TextMuted
            )
        }
    }
}
