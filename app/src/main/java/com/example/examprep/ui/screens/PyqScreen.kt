package com.example.examprep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.examprep.model.ExamCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PyqScreen(
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
                        text = "PYQ",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("pyq_back_button")
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
                .testTag("pyq_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                val count = setCountsByCategory[category.id] ?: 0
                CategoryListItemCard(
                    title = category.name,
                    icon = pyqCategoryIcon(category.id),
                    badgeText = if (count > 0) "$count Sets" else null,
                    testTag = "pyq_category_${category.id}",
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

private fun pyqCategoryIcon(categoryId: String): ImageVector = when (categoryId) {
    "pyq_osssc" -> Icons.Default.Badge
    "pyq_ossc" -> Icons.Default.School
    "pyq_opsc" -> Icons.Default.AccountBalance
    else -> Icons.Default.HistoryEdu
}
