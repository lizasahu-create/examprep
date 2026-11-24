package com.example.examprep.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.model.ExamResultSummary
import com.example.examprep.model.MockTestSet
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.components.TagBadge
import com.example.examprep.ui.theme.LocalExamFeedbackColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    mockTestSet: MockTestSet,
    onExitExam: () -> Unit,
    onFinishExam: (ExamResultSummary) -> Unit
) {
    val questions = mockTestSet.questions
    val totalDurationSeconds = (mockTestSet.meta.durationMinutes * 60).coerceAtLeast(60)

    var currentIndex by remember(mockTestSet.meta.id) { mutableIntStateOf(0) }
    val selectedAnswers = remember(mockTestSet.meta.id) { mutableStateMapOf<Int, Int>() }
    val markedForReview = remember(mockTestSet.meta.id) { mutableStateMapOf<Int, Boolean>() }
    var remainingSeconds by remember(mockTestSet.meta.id) { mutableIntStateOf(totalDurationSeconds) }
    var hasSubmitted by remember(mockTestSet.meta.id) { mutableStateOf(false) }

    var showPaletteSheet by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    fun submitNow() {
        if (hasSubmitted) return
        hasSubmitted = true
        val timeTaken = (totalDurationSeconds - remainingSeconds).coerceIn(0, totalDurationSeconds)
        onFinishExam(
            ExamResultSummary(
                set = mockTestSet,
                userAnswers = selectedAnswers.toMap(),
                timeTakenSeconds = timeTaken
            )
        )
    }

    LaunchedEffect(mockTestSet.meta.id, hasSubmitted) {
        while (!hasSubmitted && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
        }
        if (!hasSubmitted && remainingSeconds <= 0) {
            submitNow()
        }
    }

    BackHandler {
        showExitDialog = true
    }

    val currentQuestion = questions.getOrNull(currentIndex)
    val isLowTime = remainingSeconds <= 300
    val scrollState = rememberScrollState()
    LaunchedEffect(currentIndex) {
        scrollState.scrollTo(0)
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showExitDialog = true },
                            modifier = Modifier.testTag("exam_exit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Exam",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mockTestSet.meta.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Answered ${selectedAnswers.size}/${questions.size}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Countdown Timer Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isLowTime) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                            modifier = Modifier.testTag("exam_timer_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Time Remaining",
                                    tint = if (isLowTime) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatTimer(remainingSeconds),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isLowTime) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { showPaletteSheet = true },
                            modifier = Modifier.testTag("exam_palette_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Question Palette",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        TextButton(
                            onClick = { showSubmitDialog = true },
                            modifier = Modifier.testTag("exam_submit_top_button")
                        ) {
                            Text(
                                text = "Submit",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = {
                            if (questions.isNotEmpty()) (currentIndex + 1).toFloat() / questions.size else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
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
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (currentIndex > 0) currentIndex -= 1 },
                        enabled = currentIndex > 0,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("exam_prev_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Question",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Prev", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { selectedAnswers.remove(currentIndex) },
                        enabled = selectedAnswers.containsKey(currentIndex),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("exam_clear_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Response",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            if (currentIndex < questions.lastIndex) {
                                currentIndex += 1
                            } else {
                                showSubmitDialog = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("exam_next_button")
                    ) {
                        Text(
                            text = if (currentIndex < questions.lastIndex) "Save & Next" else "Submit Exam",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentIndex < questions.lastIndex) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Question",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (currentQuestion != null) {
            val isMarked = markedForReview[currentIndex] == true
            val selectedOpt = selectedAnswers[currentIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("exam_screen")
            ) {
                // Header row: Question Number, Section, Mark for Review
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        TagBadge(
                            text = "Q.${currentQuestion.number} / ${questions.size}",
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            textColor = MaterialTheme.colorScheme.onPrimary
                        )
                        if (currentQuestion.section.isNotBlank()) {
                            Text(
                                text = currentQuestion.section,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    FilterChip(
                        selected = isMarked,
                        onClick = {
                            if (isMarked) {
                                markedForReview.remove(currentIndex)
                            } else {
                                markedForReview[currentIndex] = true
                            }
                        },
                        label = {
                            Text(
                                text = if (isMarked) "Marked" else "Review",
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Mark for review",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.testTag("exam_mark_review_chip")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Optional Passage / Data Table Context
                if (!currentQuestion.contextText.isNullOrBlank()) {
                    QuestionContextCard(contextText = currentQuestion.contextText)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Question Stem + Optional Diagram Image
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1.dp
                ) {
                    QuestionStemContent(
                        questionText = currentQuestion.questionText,
                        fontSizeSp = 16f,
                        lineHeightSp = 24f
                    )

                    if (!currentQuestion.imageAssetPath.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        QuestionDiagramImage(
                            assetPath = currentQuestion.imageAssetPath,
                            contentDescription = "Diagram for question ${currentQuestion.number}"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4 MCQ Options (Exam Mode: NO correct answer revealed!)
                val optionLabels = listOf("A", "B", "C", "D")
                val feedbackColors = LocalExamFeedbackColors.current

                currentQuestion.options.forEachIndexed { optIdx, optionText ->
                    val isSelected = selectedOpt == optIdx
                    val borderColor = if (isSelected) {
                        feedbackColors.selectedBorder
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                    val containerColor = if (isSelected) {
                        feedbackColors.selectedContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                    Surface(
                        onClick = { selectedAnswers[currentIndex] = optIdx },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .defaultMinSize(minHeight = 56.dp)
                            .testTag("exam_option_${optionLabels.getOrElse(optIdx) { optIdx.toString() }}"),
                        shape = RoundedCornerShape(14.dp),
                        color = containerColor,
                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionLabels.getOrElse(optIdx) { "${optIdx + 1}" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optionText,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
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

    // Question Palette Bottom Sheet
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question Palette",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${selectedAnswers.size} / ${questions.size} Answered",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PaletteLegendDot(
                        color = MaterialTheme.colorScheme.primary,
                        label = "Answered (${selectedAnswers.size})"
                    )
                    PaletteLegendDot(
                        color = MaterialTheme.colorScheme.secondary,
                        label = "Review (${markedForReview.size})"
                    )
                    PaletteLegendDot(
                        color = MaterialTheme.colorScheme.outline,
                        label = "Unanswered (${questions.size - selectedAnswers.size})"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 48.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(questions) { idx, q ->
                        val answered = selectedAnswers.containsKey(idx)
                        val review = markedForReview[idx] == true
                        val isCurrent = idx == currentIndex

                        val bg = when {
                            review && answered -> MaterialTheme.colorScheme.tertiary
                            review -> MaterialTheme.colorScheme.secondary
                            answered -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val fg = when {
                            review || answered -> Color.White
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg)
                                .then(
                                    if (isCurrent) {
                                        Modifier.border(
                                            2.dp,
                                            MaterialTheme.colorScheme.onSurface,
                                            RoundedCornerShape(10.dp)
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable {
                                    currentIndex = idx
                                    showPaletteSheet = false
                                }
                                .testTag("palette_q_${q.number}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${q.number}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = fg
                            )
                        }
                    }
                }
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        val answered = selectedAnswers.size
        val unanswered = questions.size - answered
        val marked = markedForReview.size
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = {
                Text(
                    text = "Submit Exam?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Once submitted, your score and correct answers will be shown.")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Answered: $answered / ${questions.size}", fontWeight = FontWeight.SemiBold)
                    Text("Unanswered: $unanswered")
                    if (marked > 0) {
                        Text("Marked for Review: $marked")
                    }
                    Text("Time Remaining: ${formatTimer(remainingSeconds)}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        submitNow()
                    },
                    modifier = Modifier.testTag("confirm_submit_exam_button")
                ) {
                    Text("Submit Now")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSubmitDialog = false },
                    modifier = Modifier.testTag("cancel_submit_exam_button")
                ) {
                    Text("Continue Exam")
                }
            }
        )
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "Leave Exam?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Your current progress in this timed exam will not be saved if you exit now.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onExitExam()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.testTag("confirm_exit_exam_button")
                ) {
                    Text("Exit Exam")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false },
                    modifier = Modifier.testTag("cancel_exit_exam_button")
                ) {
                    Text("Stay")
                }
            }
        )
    }
}

@Composable
internal fun QuestionStemContent(
    questionText: String,
    fontSizeSp: Float,
    lineHeightSp: Float
) {
    val lines = remember(questionText) { questionText.lines() }
    val hasStructuredRows = remember(questionText) {
        lines.size > 1 && lines.any { it.contains(" | ") }
    }

    if (!hasStructuredRows) {
        Text(
            text = questionText,
            fontSize = fontSizeSp.sp,
            lineHeight = lineHeightSp.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        lines.forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEach
            if (line.contains(" | ")) {
                val parts = line.split(" | ", limit = 2)
                val left = parts.getOrElse(0) { "" }.trim()
                val right = parts.getOrElse(1) { "" }.trim()
                val isHeader = left.startsWith("List-I", ignoreCase = true) ||
                    right.startsWith("List-II", ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isHeader) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = left,
                            fontSize = (fontSizeSp - 2f).coerceAtLeast(12f).sp,
                            lineHeight = (lineHeightSp - 4f).coerceAtLeast(17f).sp,
                            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Medium,
                            color = if (isHeader) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = right,
                            fontSize = (fontSizeSp - 2f).coerceAtLeast(12f).sp,
                            lineHeight = (lineHeightSp - 4f).coerceAtLeast(17f).sp,
                            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Medium,
                            color = if (isHeader) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Text(
                    text = line,
                    fontSize = fontSizeSp.sp,
                    lineHeight = lineHeightSp.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
internal fun QuestionContextCard(
    contextText: String,
    initiallyExpanded: Boolean = true
) {
    var expanded by remember(contextText) { mutableStateOf(initiallyExpanded) }
    val lines = remember(contextText) { contextText.lines() }
    val headerLine = lines.firstOrNull().orEmpty()
    val bodyLines = lines.drop(1)
    val looksLikeTable = remember(contextText) { contextText.contains(" | ") }

    PaperCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .defaultMinSize(minHeight = 36.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = headerLine.ifBlank { "Passage / Data Reference" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse passage" else "Expand passage",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (expanded && bodyLines.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            bodyLines.forEach { line ->
                val isTableRow = line.contains(" | ")
                Text(
                    text = line,
                    fontSize = if (isTableRow) 12.sp else 13.sp,
                    lineHeight = if (isTableRow) 18.sp else 20.sp,
                    fontFamily = if (looksLikeTable && isTableRow) FontFamily.Monospace else FontFamily.Default,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
internal fun QuestionDiagramImage(
    assetPath: String,
    contentDescription: String
) {
    val context = LocalContext.current
    val imageBitmap: ImageBitmap? = remember(assetPath) {
        runCatching {
            context.assets.open(assetPath).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        }.getOrNull()
    }

    if (imageBitmap != null) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 90.dp, max = 270.dp)
                    .padding(10.dp)
            )
        }
    }
}

@Composable
private fun PaletteLegendDot(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

internal fun formatTimer(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val hrs = safe / 3600
    val mins = (safe % 3600) / 60
    val secs = safe % 60
    return if (hrs > 0) {
        String.format(java.util.Locale.US, "%d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
    }
}
