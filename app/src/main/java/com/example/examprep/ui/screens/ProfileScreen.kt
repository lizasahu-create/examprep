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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProgress: UserProgress,
    isAdminUser: Boolean,
    onAdminToggle: (Boolean) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showProgressDialog) {
        AlertDialog(
            onDismissRequest = { showProgressDialog = false },
            title = { Text("My Preparation Progress", fontWeight = FontWeight.Bold, color = PrimaryNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Total Mock Tests: ${userProgress.totalTests}")
                    Text("• Completed Tests: ${userProgress.completedTests}")
                    Text("• Overall Accuracy: ${userProgress.accuracyPercent}%")
                    Text("• Study Streak: ${userProgress.streakDays} Days")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Recommendation: Focus on Odisha GK and Mathematics Sectional papers to boost your accuracy to 90%+.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showProgressDialog = false }) {
                    Text("Done", color = PrimaryNavy)
                }
            },
            containerColor = PaperCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About ExamPrep", fontWeight = FontWeight.Bold, color = PrimaryNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Version 1.0.0 (Ladybug Starter)")
                    Text("ExamPrep is a comprehensive competitive exam preparation platform featuring timed MCQ exams, sectional study sheets, and previous year papers with a clean paper aesthetic.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Created for student success.", fontSize = 12.sp, color = TextMuted)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = PrimaryNavy)
                }
            },
            containerColor = PaperCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile & Settings", fontWeight = FontWeight.Bold, color = PrimaryNavy)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        containerColor = PaperBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("profile_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Profile Card matching Mockup #11
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = PaperCard,
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(PrimaryNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = if (isAdminUser) "Admin User" else userProgress.studentName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isAdminUser) "admin@gmail.com" else userProgress.email,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isAdminUser) "Role: Administrator" else "Student • Aspirant",
                            fontSize = 11.sp,
                            color = AccentOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Menu Items List
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = PaperCard,
                elevation = 1.dp
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Analytics,
                    title = "My Progress",
                    onClick = { showProgressDialog = true }
                )

                HorizontalDivider(color = PaperBorder)

                ProfileMenuItem(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Admin Dashboard Panel",
                    subtitle = if (isAdminUser) "Active" else "Switch to Admin mode",
                    onClick = {
                        if (!isAdminUser) onAdminToggle(true)
                        onNavigateToAdmin()
                    }
                )

                HorizontalDivider(color = PaperBorder)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = PrimaryNavy)
                        Spacer(modifier = Modifier.width(14.dp))
                        Text("Notifications", fontSize = 15.sp, color = TextDark)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryNavy, checkedTrackColor = Color(0xFFD4E1F8))
                    )
                }

                HorizontalDivider(color = PaperBorder)

                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    title = "About App",
                    onClick = { showAboutDialog = true }
                )

                HorizontalDivider(color = PaperBorder)

                ProfileMenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Logout",
                    titleColor = ErrorRed,
                    iconTint = ErrorRed,
                    onClick = onLogout
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = TextDark,
    iconTint: Color = PrimaryNavy,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = titleColor)
                if (subtitle != null) {
                    Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
                }
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}
