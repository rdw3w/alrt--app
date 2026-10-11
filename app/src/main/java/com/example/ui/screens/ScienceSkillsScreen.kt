package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScienceCard
import com.example.data.model.Subject
import com.example.ui.components.GlassCard
import com.example.ui.theme.RudraCoral
import com.example.ui.theme.RudraCyanDark
import com.example.ui.theme.RudraCyanNeon
import com.example.ui.theme.RudraEmerald
import com.example.ui.theme.RudraGold
import com.example.ui.theme.RudraNavyBorder
import com.example.ui.theme.RudraNavyCard
import com.example.ui.theme.RudraNavyCardElevated
import com.example.ui.theme.RudraNavyDarkest
import com.example.ui.theme.RudraTextMuted
import com.example.ui.theme.RudraTextPrimary
import com.example.ui.theme.RudraTextSecondary
import com.example.ui.theme.RudraVioletNeon
import com.example.ui.viewmodel.RudraViewModel

@Composable
fun ScienceSkillsScreen(
    viewModel: RudraViewModel,
    onStartScienceTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi by viewModel.isHindi.collectAsState()
    val quizAnswers by viewModel.quizAnswers.collectAsState()
    val cards = viewModel.scienceCards
    val quizList = viewModel.scienceQuiz

    var selectedTab by remember { mutableStateOf("CARDS") } // CARDS, QUIZ
    var selectedCategory by remember { mutableStateOf("ALL") } // ALL, PHYSICS, CHEMISTRY, BIOLOGY

    val filteredCards = remember(cards, selectedCategory) {
        if (selectedCategory == "ALL") cards else cards.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RudraNavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Science Hero Banner
        item {
            GlassCard(
                borderColor = RudraEmerald,
                backgroundColor = RudraNavyCardElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(RudraEmerald.copy(alpha = 0.2f))
                                    .border(1.dp, RudraEmerald, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = "Science",
                                    tint = RudraEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "सामान्य विज्ञान हब (Science Skills)" else "Science Mastery & Skills",
                                    color = RudraEmerald,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isHindi) "दैनिक 1 घंटा अध्ययन लक्ष्य (12:30–1:30 PM)" else "Daily 1.0 Hour Target (12:30–1:30 PM)",
                                    color = RudraTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.setSelectedSubject(Subject.SCIENCE)
                                viewModel.setTimerDurationMinutes(60)
                                onStartScienceTimer()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RudraEmerald,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_quick_science_timer")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("1h Study", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Tabs: Concept Flashcards vs Practice Quiz
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == "CARDS") RudraCyanNeon else RudraNavyCardElevated)
                        .border(1.dp, if (selectedTab == "CARDS") RudraCyanNeon else RudraNavyBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedTab = "CARDS" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = if (selectedTab == "CARDS") Color.Black else RudraTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "महत्वपूर्ण सूत्र व तथ्य" else "High-Yield Facts",
                            color = if (selectedTab == "CARDS") Color.Black else RudraTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == "QUIZ") RudraVioletNeon else RudraNavyCardElevated)
                        .border(1.dp, if (selectedTab == "QUIZ") RudraVioletNeon else RudraNavyBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedTab = "QUIZ" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Quiz,
                            contentDescription = null,
                            tint = if (selectedTab == "QUIZ") Color.Black else RudraTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "त्वरित क्विज़ (Quiz)" else "Speed Quiz",
                            color = if (selectedTab == "QUIZ") Color.Black else RudraTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (selectedTab == "CARDS") {
            // Category Chips for Cards
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val catList = listOf(
                        "ALL" to (if (isHindi) "सभी (All)" else "All"),
                        "PHYSICS" to (if (isHindi) "भौतिक विज्ञान" else "Physics"),
                        "CHEMISTRY" to (if (isHindi) "रसायन विज्ञान" else "Chemistry"),
                        "BIOLOGY" to (if (isHindi) "जीव विज्ञान" else "Biology")
                    )

                    items(catList) { (key, label) ->
                        FilterChip(
                            selected = selectedCategory == key,
                            onClick = { selectedCategory = key },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RudraEmerald,
                                selectedLabelColor = Color.Black,
                                containerColor = RudraNavyCardElevated,
                                labelColor = RudraTextSecondary
                            )
                        )
                    }
                }
            }

            items(filteredCards, key = { it.id }) { card ->
                ScienceConceptCard(card = card, isHindi = isHindi)
            }
        } else {
            // Science Quiz Questions
            items(quizList, key = { it.id }) { q ->
                ScienceQuizItem(
                    question = q,
                    selectedOption = quizAnswers[q.id],
                    isHindi = isHindi,
                    onSelect = { optionIndex ->
                        viewModel.answerQuiz(q.id, optionIndex)
                    }
                )
            }
        }
    }
}

@Composable
fun ScienceConceptCard(
    card: ScienceCard,
    isHindi: Boolean
) {
    val catColor = when (card.category) {
        "PHYSICS" -> RudraCyanNeon
        "CHEMISTRY" -> RudraGold
        "BIOLOGY" -> RudraEmerald
        else -> RudraVioletNeon
    }

    GlassCard(
        borderColor = catColor.copy(alpha = 0.4f),
        backgroundColor = RudraNavyCard
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(catColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = card.category,
                        color = catColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "⭐ ${card.examImportance}",
                    color = RudraGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isHindi) card.titleHi else card.titleEn,
                color = RudraTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Highlight box for formula / fact
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(RudraNavyCardElevated)
                    .border(1.dp, RudraNavyBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = card.formulaOrFact,
                    color = RudraCyanNeon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isHindi) card.explanationHi else card.explanationEn,
                color = RudraTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun ScienceQuizItem(
    question: com.example.data.model.ScienceQuizQuestion,
    selectedOption: Int?,
    isHindi: Boolean,
    onSelect: (Int) -> Unit
) {
    val isAnswered = selectedOption != null
    val options = if (isHindi) question.optionsHi else question.optionsEn

    GlassCard(
        borderColor = if (isAnswered) {
            if (selectedOption == question.correctIndex) RudraEmerald else RudraCoral
        } else RudraNavyBorder,
        backgroundColor = RudraNavyCard
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Q${question.id}. " + if (isHindi) question.questionHi else question.questionEn,
                color = RudraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            options.forEachIndexed { index, optionText ->
                val isSelected = selectedOption == index
                val isCorrect = index == question.correctIndex

                val borderColor = when {
                    !isAnswered -> RudraNavyBorder
                    isSelected && isCorrect -> RudraEmerald
                    isSelected && !isCorrect -> RudraCoral
                    !isSelected && isCorrect -> RudraEmerald
                    else -> RudraNavyBorder
                }

                val bgColor = when {
                    !isAnswered -> if (isSelected) RudraCyanDark else RudraNavyCardElevated
                    isSelected && isCorrect -> RudraEmerald.copy(alpha = 0.2f)
                    isSelected && !isCorrect -> RudraCoral.copy(alpha = 0.2f)
                    !isSelected && isCorrect -> RudraEmerald.copy(alpha = 0.1f)
                    else -> RudraNavyCardElevated
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isAnswered) { onSelect(index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${('A' + index)}. $optionText",
                            color = RudraTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )

                        if (isAnswered) {
                            if (isCorrect) {
                                Icon(Icons.Default.Check, contentDescription = "Correct", tint = RudraEmerald, modifier = Modifier.size(16.dp))
                            } else if (isSelected) {
                                Icon(Icons.Default.Close, contentDescription = "Wrong", tint = RudraCoral, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            if (isAnswered) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(RudraNavyCardElevated)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "💡 " + if (isHindi) question.explanationHi else question.explanationEn,
                        color = RudraGold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
