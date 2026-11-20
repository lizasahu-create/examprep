package com.example.examprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.data.ExamPrepRepository
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBackClick: () -> Unit
) {
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf("General Knowledge") }
    var newQuestionText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanationText by remember { mutableStateOf("") }
    var successToast by remember { mutableStateOf<String?>(null) }

    val metrics = ExamPrepRepository.adminMetrics
    val activities = remember { mutableStateListOf(*ExamPrepRepository.adminActivityLogs.toTypedArray()) }

    if (showAddQuestionDialog) {
        AlertDialog(
            onDismissRequest = { showAddQuestionDialog = false },
            title = {
                Text(
                    text = "Add Practice Question",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = selectedSubject,
                        onValueChange = { selectedSubject = it },
                        label = { Text("Subject Category") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newQuestionText,
                        onValueChange = { newQuestionText = it },
                        label = { Text("Question Statement") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = optA,
                        onValueChange = { optA = it },
                        label = { Text("Option A") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = optB,
                        onValueChange = { optB = it },
                        label = { Text("Option B") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = optC,
                        onValueChange = { optC = it },
                        label = { Text("Option C") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = optD,
                        onValueChange = { optD = it },
                        label = { Text("Option D") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Correct Option:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        val labels = listOf("A", "B", "C", "D")
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            labels.forEachIndexed { idx, label ->
                                FilterChip(
                                    selected = correctIndex == idx,
                                    onClick = { correctIndex = idx },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = explanationText,
                        onValueChange = { explanationText = it },
                        label = { Text("Explanation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newQuestionText.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                            ExamPrepRepository.addNewQuestion(
                                subject = selectedSubject,
                                question = newQuestionText,
                                options = listOf(
                                    optA,
                                    optB,
                                    if (optC.isBlank()) "None of above" else optC,
                                    if (optD.isBlank()) "All of above" else optD
                                ),
                                correctIndex = correctIndex,
                                explanation = if (explanationText.isBlank()) "Verified correct answer." else explanationText
                            )
                            activities.clear()
                            activities.addAll(ExamPrepRepository.adminActivityLogs)
                            successToast = "Question successfully added to database!"
                            showAddQuestionDialog = false
                            newQuestionText = ""
                            optA = ""
                            optB = ""
                            optC = ""
                            optD = ""
                            explanationText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("Save Question")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddQuestionDialog = false }) {
                    Text("Cancel", color = TextMuted)
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
                    Column {
                        Text("ExamPrep Admin", fontWeight = FontWeight.Bold, color = PrimaryNavy)
                        Text("admin@gmail.com", fontSize = 11.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryNavy)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddQuestionDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Question", tint = PrimaryNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        snackbarHost = {
            if (successToast != null) {
                Snackbar(modifier = Modifier.padding(16.dp)) {
                    Text(successToast ?: "")
                }
            }
        },
        containerColor = PaperBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("admin_content"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metrics Grid matching Mockup #8
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dashboard Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Questions",
                        value = "${metrics.totalQuestions}",
                        icon = Icons.Default.Quiz,
                        tint = PrimaryNavy
                    )
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Practice Sets",
                        value = "${metrics.practiceSets}",
                        icon = Icons.Default.Assignment,
                        tint = AccentOrange
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Sectional PDFs",
                        value = "${metrics.sectionalPdfs}",
                        icon = Icons.Default.MenuBook,
                        tint = SuccessGreen
                    )
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "PYQ Files",
                        value = "${metrics.pyqFiles}",
                        icon = Icons.Default.Description,
                        tint = Color(0xFF8B5CF6)
                    )
                }
            }

            // Quick Actions
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showAddQuestionDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_add_question_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Question to Database", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Recent Activity matching Mockup #8
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Recent Activity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            }

            items(activities) { act ->
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = PaperCard,
                    elevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = act.activity,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = act.date,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8EEF8), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = act.user,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryNavy
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    PaperCard(
        modifier = modifier,
        backgroundColor = PaperCard,
        elevation = 1.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(tint.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
