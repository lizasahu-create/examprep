package com.example.examprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.model.ExamCategory
import com.example.examprep.model.MockTestSetMeta
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.components.TagBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: ExamCategory,
    sets: List<MockTestSetMeta>,
    onBackClick: () -> Unit,
    onStartSet: (MockTestSetMeta) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (sets.isNotEmpty()) {
                                "${category.sectionType.displayTitle} • ${sets.size} Exam Sets"
                            } else {
                                category.sectionType.displayTitle
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("category_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (sets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .testTag("category_detail_screen"),
                contentAlignment = Alignment.Center
            ) {
                PaperCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TagBadge(text = category.sectionType.displayTitle)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = category.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No questions added yet.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .testTag("category_detail_screen"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp)
            ) {
                itemsIndexed(sets, key = { _, meta -> meta.id }) { index, meta ->
                    MockTestSetCard(
                        setIndex = index + 1,
                        meta = meta,
                        onStartClick = { onStartSet(meta) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MockTestSetCard(
    setIndex: Int,
    meta: MockTestSetMeta,
    onStartClick: () -> Unit
) {
    PaperCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mock_set_card_${meta.id}"),
        elevation = 2.dp,
        onClick = onStartClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$setIndex",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meta.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TagBadge(
                        text = "${meta.questionCount} Qs",
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        textColor = MaterialTheme.colorScheme.primary
                    )
                    TagBadge(
                        text = "${meta.durationMinutes} Min",
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    TagBadge(
                        text = "-${formatDecimal(meta.negativeMarking)}",
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Timed Exam • Answers after submit",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onStartClick,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp)
                    .testTag("start_set_${meta.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start ${meta.title}",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Start Exam",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

internal fun formatDecimal(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(java.util.Locale.US, "%.2f", value)
    }
}
