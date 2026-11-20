package com.example.examprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.examprep.model.PreviousYearPaper
import com.example.examprep.ui.components.PaperCard
import com.example.examprep.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PyqScreen(
    papers: List<PreviousYearPaper>,
    onBackClick: () -> Unit,
    onOpenPaper: (PreviousYearPaper) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val downloadedMap = remember { mutableStateMapOf<String, Boolean>() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val filterOptions = listOf("All", "2023", "2022", "2021", "2020", "2019")

    val filteredPapers = remember(selectedFilter, papers) {
        if (selectedFilter == "All") papers
        else papers.filter { it.year.toString() == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Previous Year Questions",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
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
                            tint = PrimaryNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        snackbarHost = {
            if (snackbarMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("OK", color = AccentOrange)
                        }
                    }
                ) {
                    Text(snackbarMessage ?: "")
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
                .testTag("pyq_list"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Year Filter Chips Row matching Mockup #7
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(filterOptions) { filter ->
                        val isSelected = filter == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryNavy,
                                selectedLabelColor = Color.White,
                                containerColor = PaperCard,
                                labelColor = TextDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) PrimaryNavy else PaperBorder
                            )
                        )
                    }
                }
            }

            items(filteredPapers) { paper ->
                val isDownloaded = downloadedMap[paper.id] == true

                PaperCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pyq_item_${paper.id}"),
                    backgroundColor = PaperCard,
                    elevation = 1.dp,
                    onClick = { onOpenPaper(paper) }
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
                                    .size(44.dp)
                                    .background(
                                        color = Color(0xFFF1EFEA),
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "${paper.examName} ${paper.year}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "PDF • ${paper.pagesCount} Pages",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = paper.paperType,
                                    fontSize = 11.sp,
                                    color = PrimaryNavyLight
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                downloadedMap[paper.id] = !isDownloaded
                                snackbarMessage = if (!isDownloaded) {
                                    "${paper.examName} ${paper.year} downloaded for offline study!"
                                } else {
                                    "Removed offline download."
                                }
                            },
                            modifier = Modifier.testTag("download_pyq_${paper.id}")
                        ) {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                                contentDescription = "Download paper",
                                tint = if (isDownloaded) SuccessGreen else PrimaryNavy
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
