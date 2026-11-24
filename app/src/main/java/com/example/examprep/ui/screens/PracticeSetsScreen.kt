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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.model.ExamCategory
import com.example.examprep.ui.components.PaperCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeSetsScreen(
    categories: List<ExamCategory>,
    setCountsByCategory: Map<String, Int> = emptyMap(),
    onBackClick: () -> Unit,
    onCategoryClick: (ExamCategory) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Practice Mock",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("practice_back_button")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("practice_sets_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                val count = setCountsByCategory[category.id] ?: 0
                CategoryListItemCard(
                    title = category.name,
                    icon = practiceCategoryIcon(category.id),
                    badgeText = if (count > 0) "$count Sets" else null,
                    testTag = "practice_category_${category.id}",
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

private fun practiceCategoryIcon(categoryId: String): ImageVector = when (categoryId) {
    "practice_full_length" -> Icons.Default.Assignment
    "practice_gk" -> Icons.Default.Public
    "practice_math_reasoning" -> Icons.Default.Calculate
    "practice_odia_english" -> Icons.Default.Translate
    else -> Icons.Default.Timer
}

@Composable
internal fun CategoryListItemCard(
    title: String,
    icon: ImageVector,
    testTag: String,
    badgeText: String? = null,
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
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!badgeText.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open $title",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
