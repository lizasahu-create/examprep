package com.example.examprep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examprep.model.ExamCategory
import com.example.examprep.ui.components.PaperCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionalScreen(
    categories: List<ExamCategory>,
    onBackClick: () -> Unit,
    onCategoryClick: (ExamCategory) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCategories = remember(categories, searchQuery) {
        if (searchQuery.isBlank()) {
            categories
        } else {
            categories.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sectional",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("sectional_back_button")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("sectional_screen")
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("sectional_search_input"),
                placeholder = {
                    Text(
                        text = "Search categories...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.testTag("sectional_search_clear")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (filteredCategories.isEmpty()) {
                PaperCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No matching categories.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                ) {
                    items(filteredCategories, key = { it.id }) { category ->
                        CategoryListItemCard(
                            title = category.name,
                            icon = sectionalCategoryIcon(category.id),
                            testTag = "sectional_category_${category.id}",
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }
        }
    }
}

private fun sectionalCategoryIcon(categoryId: String): ImageVector = when (categoryId) {
    "sectional_computer" -> Icons.Default.Computer
    "sectional_economics" -> Icons.Default.AccountBalance
    "sectional_english" -> Icons.Default.Abc
    "sectional_geography" -> Icons.Default.Terrain
    "sectional_history" -> Icons.Default.HistoryEdu
    "sectional_math" -> Icons.Default.Calculate
    "sectional_odia" -> Icons.Default.Translate
    "sectional_odisha_gk" -> Icons.Default.LocationOn
    "sectional_polity" -> Icons.Default.Gavel
    "sectional_reasoning" -> Icons.Default.Psychology
    else -> Icons.Default.MenuBook
}
