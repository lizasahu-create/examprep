package com.example.examprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultScreen(
    practiceSet: PracticeSet,
    userAnswers: Map<Int, Int>,
    timeTakenSeconds: Int,
    onRetakeTest: () -> Unit,
    onDone: () -> Unit
) {
    var correctCount = 0
    var incorrectCount = 0
    var skippedCount = 0

    practiceSet.questions.forEachIndexed { index, question ->
        val chosen = userAnswers[index]
        if (chosen == null) {
            skippedCount++
        } else if (chosen == question.correctIndex) {
            correctCount++
        } else {
            incorrectCount++
        }
    }

    val totalQuestions = practiceSet.questions.size
    val accuracy = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
    val timeMinutes = timeTakenSeconds / 60
    val timeSeconds = timeTakenSeconds % 60
    val timeFormatted = String.format("%02d:%02d", timeMinutes, timeSeconds)

    val bookmarkedQuestions = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Test Summary & Analysis",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDone,
                        modifier = Modifier.testTag("result_done_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Done", tint = PrimaryNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        bottomBar = {
            Surface(
                color = PaperCard,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetakeTest,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("retake_test_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake Test")
                    }

                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("return_home_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                    ) {
                        Text("Back to Sets", color = Color.White, fontWeight = FontWeight.Bold)
                    }
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
                .testTag("result_list"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Score Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = PaperCard,
                    elevation = 2.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Score: $correctCount / $totalQuestions",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (accuracy >= 70) "Excellent Performance! 🏆" else "Good Effort! Keep practicing! 📚",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (accuracy >= 70) SuccessGreen else AccentOrange
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ResultStat(label = "Correct", count = "$correctCount", color = SuccessGreen)
                            ResultStat(label = "Incorrect", count = "$incorrectCount", color = ErrorRed)
                            ResultStat(label = "Skipped", count = "$skippedCount", color = TextMuted)
                            ResultStat(label = "Time", count = timeFormatted, color = PrimaryNavy)
                        }
                    }
                }
            }

            // Solutions Title
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Question-by-Question Review & Solutions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            }

            itemsIndexed(practiceSet.questions) { index, question ->
                val chosenIndex = userAnswers[index]
                val isCorrect = chosenIndex == question.correctIndex
                val isSkipped = chosenIndex == null

                PaperCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("solution_card_$index"),
                    backgroundColor = PaperCard,
                    borderColor = when {
                        isCorrect -> Color(0xFFC7F0D2)
                        isSkipped -> PaperBorder
                        else -> Color(0xFFFFD5D5)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCorrect -> SuccessGreen
                                            isSkipped -> TextMuted
                                            else -> ErrorRed
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isCorrect -> "Correct (+1.0)"
                                    isSkipped -> "Skipped (0.0)"
                                    else -> "Incorrect (-0.25)"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when {
                                    isCorrect -> SuccessGreen
                                    isSkipped -> TextMuted
                                    else -> ErrorRed
                                }
                            )
                        }

                        IconButton(
                            onClick = {
                                val cur = bookmarkedQuestions[question.id] ?: false
                                bookmarkedQuestions[question.id] = !cur
                            }
                        ) {
                            Icon(
                                imageVector = if (bookmarkedQuestions[question.id] == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = AccentOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = question.questionText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Options list summary
                    val optionLabels = listOf("A", "B", "C", "D")
                    question.options.forEachIndexed { optIdx, optText ->
                        val isUserChoice = chosenIndex == optIdx
                        val isRightChoice = question.correctIndex == optIdx

                        val optColor = when {
                            isRightChoice -> SuccessGreen
                            isUserChoice -> ErrorRed
                            else -> TextDark
                        }

                        val optPrefix = when {
                            isRightChoice && isUserChoice -> "✓ (Your Answer) "
                            isRightChoice -> "✓ (Correct) "
                            isUserChoice -> "✗ (Your Answer) "
                            else -> ""
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${optionLabels.getOrElse(optIdx) { "" }}. $optText $optPrefix",
                                fontSize = 13.sp,
                                color = optColor,
                                fontWeight = if (isRightChoice || isUserChoice) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Explanation Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PaperCardAlt)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Explanation:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = question.explanation,
                                fontSize = 12.sp,
                                color = TextDark,
                                lineHeight = 17.sp
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
fun ResultStat(label: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}
