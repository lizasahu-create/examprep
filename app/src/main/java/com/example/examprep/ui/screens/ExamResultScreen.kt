package com.example.examprep.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.model.ExamResultSummary
import com.example.examprep.model.McqQuestion
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.components.TagBadge
import com.example.examprep.ui.theme.LocalExamFeedbackColors

private enum class ReviewFilter(val label: String) {
    ALL("All"),
    CORRECT("Correct"),
    WRONG("Wrong"),
    SKIPPED("Skipped")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultScreen(
    result: ExamResultSummary,
    onRetakeExam: () -> Unit,
    onDoneClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(ReviewFilter.ALL) }
    val feedbackColors = LocalExamFeedbackColors.current

    val filteredItems = remember(result, selectedFilter) {
        result.set.questions.mapIndexed { idx, q -> idx to q }.filter { (idx, q) ->
            val userAns = result.userAnswers[idx]
            when (selectedFilter) {
                ReviewFilter.ALL -> true
                ReviewFilter.CORRECT -> userAns == q.correctIndex
                ReviewFilter.WRONG -> userAns != null && userAns != q.correctIndex
                ReviewFilter.SKIPPED -> userAns == null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Exam Analysis",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = result.set.meta.title,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDoneClick,
                        modifier = Modifier.testTag("result_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Sets",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetakeExam,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("result_retake_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake", fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onDoneClick,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("result_done_button")
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("exam_result_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // Overall Score & Performance Card
            item {
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Score",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${formatDecimal(result.rawScore)} / ${formatDecimal(result.maxScore)}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "+${formatDecimal(result.set.meta.marksPerQuestion)} correct • -${formatDecimal(result.set.meta.negativeMarking)} wrong",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            TagBadge(
                                text = "Accuracy ${result.accuracyPercent}%",
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Time: ${formatTimer(result.timeTakenSeconds)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResultStatPill(
                            modifier = Modifier.weight(1f),
                            label = "Correct",
                            count = result.correctCount,
                            color = feedbackColors.correct,
                            container = feedbackColors.correctContainer
                        )
                        ResultStatPill(
                            modifier = Modifier.weight(1f),
                            label = "Wrong",
                            count = result.incorrectCount,
                            color = feedbackColors.wrong,
                            container = feedbackColors.wrongContainer
                        )
                        ResultStatPill(
                            modifier = Modifier.weight(1f),
                            label = "Skipped",
                            count = result.unattemptedCount,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            container = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Section-wise Analysis Card
            if (result.sectionBreakdown.size > 1) {
                item {
                    PaperCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 1.dp
                    ) {
                        Text(
                            text = "Section-wise Analysis",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        result.sectionBreakdown.forEachIndexed { idx, sec ->
                            val ratio = if (sec.total > 0) sec.correct.toFloat() / sec.total else 0f
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sec.sectionName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${sec.correct}/${sec.total}  (${sec.incorrect}W, ${sec.unattempted}S)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = feedbackColors.correct,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                            if (idx < result.sectionBreakdown.lastIndex) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }

            // Answer Key Review Header & Filter Row
            item {
                Column {
                    Text(
                        text = "Answer Key & Review",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReviewFilter.entries.forEach { filter ->
                            val count = when (filter) {
                                ReviewFilter.ALL -> result.totalQuestions
                                ReviewFilter.CORRECT -> result.correctCount
                                ReviewFilter.WRONG -> result.incorrectCount
                                ReviewFilter.SKIPPED -> result.unattemptedCount
                            }
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text("${filter.label} ($count)", fontSize = 12.sp) },
                                modifier = Modifier.testTag("review_filter_${filter.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Filtered Question & Answer Review Cards
            items(filteredItems, key = { (_, q) -> q.number }) { (idx, question) ->
                AnswerReviewCard(
                    question = question,
                    userAnswerIndex = result.userAnswers[idx]
                )
            }
        }
    }
}

@Composable
private fun ResultStatPill(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    color: Color,
    container: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(container)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$count",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
private fun AnswerReviewCard(
    question: McqQuestion,
    userAnswerIndex: Int?
) {
    val feedbackColors = LocalExamFeedbackColors.current
    val optionLabels = listOf("A", "B", "C", "D")
    val isCorrect = userAnswerIndex == question.correctIndex
    val isSkipped = userAnswerIndex == null

    PaperCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("review_card_q_${question.number}"),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagBadge(
                text = "Q.${question.number}",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                textColor = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when {
                        isCorrect -> Icons.Default.CheckCircle
                        isSkipped -> Icons.Default.RemoveCircleOutline
                        else -> Icons.Default.Cancel
                    },
                    contentDescription = null,
                    tint = when {
                        isCorrect -> feedbackColors.correct
                        isSkipped -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> feedbackColors.wrong
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        isCorrect -> "Correct"
                        isSkipped -> "Skipped"
                        else -> "Wrong"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isCorrect -> feedbackColors.correct
                        isSkipped -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> feedbackColors.wrong
                    }
                )
            }
        }

        if (!question.contextText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            QuestionContextCard(
                contextText = question.contextText,
                initiallyExpanded = false
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        QuestionStemContent(
            questionText = question.questionText,
            fontSizeSp = 14f,
            lineHeightSp = 21f
        )

        if (!question.imageAssetPath.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            QuestionDiagramImage(
                assetPath = question.imageAssetPath,
                contentDescription = "Diagram for question ${question.number}"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        question.options.forEachIndexed { optIdx, optionText ->
            val isCorrectOption = optIdx == question.correctIndex
            val isUserWrongOption = optIdx == userAnswerIndex && !isCorrectOption

            val bgColor = when {
                isCorrectOption -> feedbackColors.correctContainer
                isUserWrongOption -> feedbackColors.wrongContainer
                else -> MaterialTheme.colorScheme.surface
            }
            val borderColor = when {
                isCorrectOption -> feedbackColors.correct
                isUserWrongOption -> feedbackColors.wrong
                else -> MaterialTheme.colorScheme.outline
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                shape = RoundedCornerShape(10.dp),
                color = bgColor,
                border = BorderStroke(if (isCorrectOption || isUserWrongOption) 1.5.dp else 1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCorrectOption -> feedbackColors.correct
                                    isUserWrongOption -> feedbackColors.wrong
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = optionLabels.getOrElse(optIdx) { "${optIdx + 1}" },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrectOption || isUserWrongOption) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = optionText,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontWeight = if (isCorrectOption || isUserWrongOption) FontWeight.SemiBold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (isCorrectOption) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Correct Answer",
                            tint = feedbackColors.correct,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (isUserWrongOption) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Your Answer",
                            tint = feedbackColors.wrong,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
