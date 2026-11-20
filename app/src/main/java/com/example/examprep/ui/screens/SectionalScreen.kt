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
import com.example.examprep.model.SectionalSubject
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionalScreen(
    subjects: List<SectionalSubject>,
    onBackClick: () -> Unit,
    onSubjectClick: (SectionalSubject) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredSubjects = remember(searchQuery, subjects) {
        if (searchQuery.isBlank()) subjects
        else subjects.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sectional Questions",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
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
                            tint = PrimaryNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        containerColor = PaperBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("sectional_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search subjects (e.g. Mathematics, Polity...)") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryNavy)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("sectional_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNavy,
                        unfocusedBorderColor = PaperBorder,
                        focusedContainerColor = PaperCard,
                        unfocusedContainerColor = PaperCard
                    )
                )
            }

            items(filteredSubjects) { subject ->
                PaperCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sectional_item_${subject.id}"),
                    backgroundColor = PaperCard,
                    elevation = 1.dp,
                    onClick = { onSubjectClick(subject) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        color = when (subject.id) {
                                            "math" -> Color(0xFFE8EEF8)
                                            "odia" -> Color(0xFFFFF2E8)
                                            "english" -> Color(0xFFEAF9F0)
                                            "polity" -> Color(0xFFF3E8FF)
                                            "history" -> Color(0xFFFEECEE)
                                            else -> Color(0xFFF6F3EB)
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (subject.id) {
                                        "math" -> Icons.Default.Calculate
                                        "odia", "english" -> Icons.Default.Translate
                                        "reasoning" -> Icons.Default.Psychology
                                        "history" -> Icons.Default.AccountBalance
                                        "geography" -> Icons.Default.Public
                                        "economics" -> Icons.Default.TrendingUp
                                        "polity" -> Icons.Default.Gavel
                                        "science" -> Icons.Default.Science
                                        "computer" -> Icons.Default.Computer
                                        else -> Icons.Default.MenuBook
                                    },
                                    contentDescription = null,
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = subject.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "PDF • ${subject.pagesCount} Pages",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = TextMuted
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
