package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.GlassCard
import com.example.ui.components.SubjectTargetItem
import com.example.ui.theme.RudraCoral
import com.example.ui.theme.RudraCyanNeon
import com.example.ui.theme.RudraEmerald
import com.example.ui.theme.RudraGold
import com.example.ui.theme.RudraNavyBorder
import com.example.ui.theme.RudraNavyCard
import com.example.ui.theme.RudraNavyCardElevated
import com.example.ui.theme.RudraNavyDarkest
import com.example.ui.theme.RudraOrange
import com.example.ui.theme.RudraTextMuted
import com.example.ui.theme.RudraTextPrimary
import com.example.ui.theme.RudraTextSecondary
import com.example.ui.theme.RudraVioletNeon
import com.example.ui.viewmodel.RudraViewModel

@Composable
fun ReportsScreen(
    viewModel: RudraViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val studySessions by viewModel.studySessions.collectAsState()

    val completedTasks = remember(tasks) { tasks.count { it.isCompleted } }
    val skippedTasks = remember(tasks) { tasks.count { it.isSkipped } }
    val missedTasks = remember(tasks) { tasks.count { it.isMissed } }
    val pendingTasks = remember(tasks) { tasks.count { !it.isCompleted && !it.isSkipped && !it.isMissed } }
    val totalTasks = remember(tasks) { tasks.size }

    val completionRate = remember(completedTasks, totalTasks) {
        if (totalTasks > 0) (completedTasks.toFloat() / totalTasks.toFloat()) * 100f else 0f
    }

    val totalPlannedStudyMinutes = remember(tasks) {
        tasks.filter { it.category == "STUDY" }.sumOf { it.targetMinutes }
    }
    val totalActualStudyMinutes = remember(studySessions) {
        studySessions.sumOf { it.durationMinutes }
    }

    val subjectMap = remember(studySessions) {
        val map = mutableMapOf<String, Int>()
        Subject.entries.forEach { map[it.key] = 0 }
        studySessions.forEach {
            val curr = map[it.subjectKey] ?: 0
            map[it.subjectKey] = curr + it.durationMinutes
        }
        map
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RudraNavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Analytics Header
        item {
            GlassCard(
                borderColor = RudraVioletNeon,
                backgroundColor = RudraNavyCardElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = RudraVioletNeon,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "प्रगति व विश्लेषण रिपोर्ट" else "Progress & Performance",
                                color = RudraVioletNeon,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isHindi)
                                "वास्तविक डेटा पर आधारित दैनिक व साप्ताहिक प्रदर्शन रिपोर्ट।"
                            else
                                "Actual tracked routine and focused study analytics.",
                            color = RudraTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Streak Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(RudraOrange.copy(alpha = 0.2f))
                            .border(1.dp, RudraOrange, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = RudraOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "7 " + if (isHindi) "दिन स्ट्रीक" else "Day Streak",
                                color = RudraOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Daily Task Breakdown Stats (Done, Skipped, Missed, Pending)
        item {
            GlassCard(
                borderColor = RudraNavyBorder,
                backgroundColor = RudraNavyCard
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isHindi) "दैनिक दिनचर्या स्थिति (Task Status)" else "Daily Routine Completion",
                        color = RudraTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatPill(
                            label = if (isHindi) "पूर्ण" else "Done",
                            count = completedTasks,
                            color = RudraEmerald
                        )
                        StatPill(
                            label = if (isHindi) "लंबित" else "Pending",
                            count = pendingTasks,
                            color = RudraCyanNeon
                        )
                        StatPill(
                            label = if (isHindi) "छोड़ा" else "Skipped",
                            count = skippedTasks,
                            color = RudraOrange
                        )
                        StatPill(
                            label = if (isHindi) "मिस" else "Missed",
                            count = missedTasks,
                            color = RudraCoral
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { completionRate / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = RudraCyanNeon,
                        trackColor = RudraNavyBorder
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${completionRate.toInt()}% " + if (isHindi) "दिनचर्या लक्ष्य पूर्ण" else "Daily Target Accomplished",
                        color = RudraTextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Planned vs Actual Study Time Comparison
        item {
            GlassCard(
                borderColor = RudraGold,
                backgroundColor = RudraNavyCard
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isHindi) "नियोजित बनाम वास्तविक अध्ययन (13 घंटे लक्ष्य)" else "Planned vs Actual Study (13.0h Target)",
                        color = RudraGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isHindi) "नियोजित अध्ययन" else "Planned Study",
                                color = RudraTextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${totalPlannedStudyMinutes / 60}h ${totalPlannedStudyMinutes % 60}m",
                                color = RudraGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "13.0 Hours Target", color = RudraTextSecondary, fontSize = 10.sp)
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(RudraNavyBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isHindi) "वास्तविक केंद्रित अध्ययन" else "Actual Focused Study",
                                color = RudraTextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${totalActualStudyMinutes / 60}h ${totalActualStudyMinutes % 60}m",
                                color = RudraEmerald,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "From Focus Timers", color = RudraTextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Subject Breakdown Cards
        item {
            Text(
                text = if (isHindi) "विषयवार अध्ययन प्रगति (Subject Targets)" else "Subject Targets Breakdown",
                color = RudraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(Subject.entries.toTypedArray()) { sub ->
            SubjectTargetItem(
                subject = sub,
                completedMinutes = subjectMap[sub.key] ?: 0,
                isHindi = isHindi
            )
        }
    }
}

@Composable
fun StatPill(
    label: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(RudraNavyCardElevated)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "$count",
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = RudraTextSecondary,
            fontSize = 10.sp
        )
    }
}
