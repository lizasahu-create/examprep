package com.example.examprep.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.model.PracticeSet
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    practiceSet: PracticeSet,
    onBackClick: () -> Unit,
    onSubmitExam: (answers: Map<Int, Int>, timeTakenSeconds: Int) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    val markedForReview = remember { mutableStateMapOf<Int, Boolean>() }
    var showQuitDialog by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showPaletteSheet by remember { mutableStateOf(false) }

    val totalDurationSeconds = practiceSet.durationMinutes * 60
    var secondsRemaining by remember { mutableIntStateOf(totalDurationSeconds) }

    // Countdown Timer
    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
        // Auto submit if timer runs out
        onSubmitExam(userAnswers.toMap(), totalDurationSeconds - secondsRemaining)
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val currentQuestion = practiceSet.questions[currentIndex]

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Quit Practice Test?", fontWeight = FontWeight.Bold, color = PrimaryNavy) },
            text = { Text("Your progress in this practice test will be lost if you leave now.") },
            confirmButton = {
                TextButton(onClick = onBackClick) {
                    Text("Quit Test", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text("Resume Test", color = PrimaryNavy)
                }
            },
            containerColor = PaperCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showSubmitDialog) {
        val answeredCount = userAnswers.size
        val unansweredCount = practiceSet.questions.size - answeredCount
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Submit Practice Test?", fontWeight = FontWeight.Bold, color = PrimaryNavy) },
            text = {
                Column {
                    Text("Are you ready to submit your answers?")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• Answered: $answeredCount", fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                    Text("• Unanswered: $unansweredCount", fontWeight = FontWeight.SemiBold, color = TextMuted)
                    Text("• Marked for Review: ${markedForReview.filter { it.value }.size}", fontWeight = FontWeight.SemiBold, color = AccentOrange)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        onSubmitExam(userAnswers.toMap(), totalDurationSeconds - secondsRemaining)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("Yes, Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = PaperCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Question Palette Bottom Sheet
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            containerColor = PaperCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Question Palette",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
                Spacer(modifier = Modifier.height(14.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(practiceSet.questions.size) { idx ->
                        val isAnswered = userAnswers.containsKey(idx)
                        val isMarked = markedForReview[idx] == true
                        val isCurrent = idx == currentIndex

                        val bg = when {
                            isCurrent -> PrimaryNavy
                            isMarked -> AccentOrange
                            isAnswered -> SuccessGreen
                            else -> PaperCardAlt
                        }

                        val textColor = when {
                            isCurrent || isMarked || isAnswered -> Color.White
                            else -> TextDark
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg)
                                .clickable {
                                    currentIndex = idx
                                    showPaletteSheet = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Practice Set",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { showQuitDialog = true },
                        modifier = Modifier.testTag("exam_quit_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quit",
                            tint = PrimaryNavy
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showPaletteSheet = true },
                        modifier = Modifier.testTag("exam_palette_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Palette",
                            tint = PrimaryNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        bottomBar = {
            Surface(
                color = PaperCard,
                tonalElevation = 6.dp,
                border = BorderStroke(1.dp, PaperBorder),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentIndex > 0) currentIndex--
                        },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("exam_prev_button")
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    OutlinedButton(
                        onClick = {
                            val cur = markedForReview[currentIndex] ?: false
                            markedForReview[currentIndex] = !cur
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (markedForReview[currentIndex] == true) AccentOrange else TextMuted
                        )
                    ) {
                        Icon(
                            imageVector = if (markedForReview[currentIndex] == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (currentIndex < practiceSet.questions.size - 1) {
                        Button(
                            onClick = { currentIndex++ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            modifier = Modifier.testTag("exam_next_button")
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    } else {
                        Button(
                            onClick = { showSubmitDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            modifier = Modifier.testTag("exam_submit_button")
                        ) {
                            Text("Submit Test", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = PaperBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("exam_content")
        ) {
            // Subheader: Q. X/Y and Time Left matching Mockup #5
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Q. ${currentIndex + 1}/${practiceSet.questions.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (secondsRemaining < 120) Color(0xFFFFEBEB) else PaperCardAlt)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (secondsRemaining < 120) ErrorRed else PrimaryNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Time Left: $timeFormatted",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (secondsRemaining < 120) ErrorRed else PrimaryNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / practiceSet.questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AccentOrange,
                trackColor = PaperBorder,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Question Card
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = PaperCard,
                elevation = 2.dp
            ) {
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Options list (A, B, C, D)
            val optionLabels = listOf("A", "B", "C", "D")
            currentQuestion.options.forEachIndexed { optIndex, optionText ->
                val isSelected = userAnswers[currentIndex] == optIndex
                val optLabel = optionLabels.getOrElse(optIndex) { "${optIndex + 1}" }

                val borderColor = if (isSelected) SuccessGreen else PaperBorder
                val cardBg = if (isSelected) Color(0xFFF1FBF4) else PaperCard

                Card(
                    onClick = {
                        userAnswers[currentIndex] = optIndex
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("option_${optLabel}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) SuccessGreen else PaperCardAlt),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else PrimaryNavy
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = optionText,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = TextDark,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
