package com.example.examprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.model.SectionalSubject
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentReaderScreen(
    title: String,
    totalPages: Int,
    subjectData: SectionalSubject? = null,
    onBackClick: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(1) }
    var isDarkReaderMode by remember { mutableStateOf(false) }
    var fontScale by remember { mutableFloatStateOf(1.0f) }
    var isBookmarked by remember { mutableStateOf(false) }

    val readerBg = if (isDarkReaderMode) Color(0xFF1E222A) else Color(0xFFFAF7F0)
    val readerText = if (isDarkReaderMode) Color(0xFFE5E7EB) else Color(0xFF1F2937)
    val pageCardBg = if (isDarkReaderMode) Color(0xFF282E39) else Color(0xFFFFFFFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                        Text(
                            text = "Page $currentPage of $totalPages",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryNavy
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isDarkReaderMode = !isDarkReaderMode }) {
                        Icon(
                            imageVector = if (isDarkReaderMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle night mode",
                            tint = PrimaryNavy
                        )
                    }
                    IconButton(onClick = {
                        fontScale = if (fontScale >= 1.3f) 1.0f else fontScale + 0.15f
                    }) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Adjust font size",
                            tint = PrimaryNavy
                        )
                    }
                    IconButton(onClick = { isBookmarked = !isBookmarked }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = AccentOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        bottomBar = {
            Surface(
                color = PaperCard,
                shadowElevation = 6.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (currentPage > 1) currentPage-- },
                        enabled = currentPage > 1,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Prev Page")
                    }

                    Text(
                        text = "Page $currentPage / $totalPages",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = PrimaryNavy
                    )

                    Button(
                        onClick = { if (currentPage < totalPages) currentPage++ },
                        enabled = currentPage < totalPages,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                    ) {
                        Text("Next Page")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        },
        containerColor = readerBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("reader_content")
        ) {
            // Paper Book Sheet Frame
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = pageCardBg,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkReaderMode) Color(0xFF3C4452) else PaperBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header inside sheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subjectData?.name ?: title,
                            fontSize = (18 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkReaderMode) Color(0xFF93C5FD) else PrimaryNavy
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isDarkReaderMode) Color(0xFF333D4E) else PaperCardAlt)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Standard Book View",
                                fontSize = 11.sp,
                                color = if (isDarkReaderMode) Color.White else TextMuted
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = if (isDarkReaderMode) Color(0xFF3C4452) else PaperBorder
                    )

                    // Render study notes or realistic book questions
                    val notes = subjectData?.studyNotes
                    if (!notes.isNullOrEmpty()) {
                        notes.forEach { note ->
                            Text(
                                text = note.title,
                                fontSize = (16 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkReaderMode) Color(0xFFFCD34D) else AccentOrange
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = note.content,
                                fontSize = (14 * fontScale).sp,
                                color = readerText,
                                lineHeight = (22 * fontScale).sp
                            )

                            if (note.formulasOrHighlights.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDarkReaderMode) Color(0xFF1E2530) else Color(0xFFF6F3EB))
                                        .padding(14.dp)
                                ) {
                                    note.formulasOrHighlights.forEach { item ->
                                        Text(
                                            text = "• $item",
                                            fontSize = (13 * fontScale).sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isDarkReaderMode) Color(0xFF86EFAC) else PrimaryNavyDark,
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            if (note.practiceQuestion != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Practice Example:",
                                    fontSize = (13 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkReaderMode) Color(0xFF6EE7B7) else SuccessGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.practiceQuestion,
                                    fontSize = (13 * fontScale).sp,
                                    color = readerText
                                )
                                if (note.practiceAnswer != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = note.practiceAnswer,
                                        fontSize = (12 * fontScale).sp,
                                        color = if (isDarkReaderMode) Color(0xFFCBD5E1) else TextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    } else {
                        // Sample printed book exam paper view matching mockup #6
                        SampleBookQuestion(
                            num = 1,
                            question = "The value of √16 is:",
                            options = listOf("(a) 2", "(b) 4", "(c) 6", "(d) 8"),
                            fontScale = fontScale,
                            readerText = readerText,
                            isDark = isDarkReaderMode
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SampleBookQuestion(
                            num = 2,
                            question = "If x + 3 = 7, then x =",
                            options = listOf("(a) 2", "(b) 3", "(c) 4", "(d) 5"),
                            fontScale = fontScale,
                            readerText = readerText,
                            isDark = isDarkReaderMode
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SampleBookQuestion(
                            num = 3,
                            question = "The area of a circle with radius r is:",
                            options = listOf("(a) 2πr", "(b) πr²", "(c) 2πr²", "(d) πr"),
                            fontScale = fontScale,
                            readerText = readerText,
                            isDark = isDarkReaderMode
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SampleBookQuestion(
    num: Int,
    question: String,
    options: List<String>,
    fontScale: Float,
    readerText: Color,
    isDark: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$num. $question",
            fontSize = (15 * fontScale).sp,
            fontWeight = FontWeight.SemiBold,
            color = readerText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = options.getOrElse(0) { "" }, fontSize = (14 * fontScale).sp, color = readerText)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = options.getOrElse(2) { "" }, fontSize = (14 * fontScale).sp, color = readerText)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = options.getOrElse(1) { "" }, fontSize = (14 * fontScale).sp, color = readerText)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = options.getOrElse(3) { "" }, fontSize = (14 * fontScale).sp, color = readerText)
            }
        }
    }
}
