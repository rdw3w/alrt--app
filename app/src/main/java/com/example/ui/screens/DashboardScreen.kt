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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoutineTask
import com.example.data.model.Subject
import com.example.data.model.TaskCategory
import com.example.ui.components.CircularProgressMeter
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
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: RudraViewModel,
    onNavigateToTimetable: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToChecklist: () -> Unit,
    onNavigateToScience: () -> Unit,
    onNavigateToReports: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val is24h by viewModel.is24HourFormat.collectAsState()
    val currentMinutes by viewModel.currentMinutesOfDay.collectAsState()
    val studySessions by viewModel.studySessions.collectAsState()
    val waterGlasses by viewModel.waterGlassesToday.collectAsState()

    // Determine current task and next task based on current minutes of day
    val sortedTasks = remember(tasks) { tasks.sortedBy { it.orderIndex } }

    val currentTask = remember(sortedTasks, currentMinutes) {
        sortedTasks.firstOrNull { task ->
            val startMin = viewModel.timeToMinutes(task.startTime)
            val endMin = viewModel.timeToMinutes(task.endTime)
            if (startMin <= endMin) {
                currentMinutes in startMin until endMin
            } else {
                // Crosses midnight (e.g. 22:30 to 05:00)
                currentMinutes >= startMin || currentMinutes < endMin
            }
        } ?: sortedTasks.firstOrNull()
    }

    val nextTask = remember(sortedTasks, currentTask) {
        if (currentTask != null) {
            val currIndex = sortedTasks.indexOf(currentTask)
            if (currIndex in sortedTasks.indices && currIndex + 1 < sortedTasks.size) {
                sortedTasks[currIndex + 1]
            } else {
                sortedTasks.firstOrNull()
            }
        } else null
    }

    // Calculations
    val completedCount = remember(tasks) { tasks.count { it.isCompleted } }
    val totalCount = remember(tasks) { tasks.size }
    val completionPercent = remember(completedCount, totalCount) {
        if (totalCount > 0) (completedCount.toFloat() / totalCount.toFloat()) * 100f else 0f
    }

    val totalPlannedStudyMinutes = remember(tasks) {
        tasks.filter { it.category == "STUDY" }.sumOf { it.targetMinutes }
    }
    val completedStudyMinutes = remember(studySessions) {
        studySessions.sumOf { it.durationMinutes }
    }
    val upcomingSlice = remember(sortedTasks) { sortedTasks.take(5) }

    // Greeting logic
    val greeting = remember(currentMinutes, isHindi) {
        when (currentMinutes) {
            in 300..719 -> if (isHindi) "शुभ प्रभात, योद्धा! 🌅" else "Good Morning, Warrior! 🌅"
            in 720..959 -> if (isHindi) "शुभ दोपहर! ☀️" else "Good Afternoon! ☀️"
            in 960..1259 -> if (isHindi) "शुभ संध्या! 🌇" else "Good Evening! 🌇"
            else -> if (isHindi) "शुभ रात्रि! 🌙" else "Good Night! 🌙"
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RudraNavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Greeting Card
        item {
            GlassCard(
                borderColor = RudraCyanDark,
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
                        Text(
                            text = greeting,
                            color = RudraGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi)
                                "प्रत्येक मिनट आपकी सफलता निर्धारित करता है। लक्ष्य 13 घंटे अध्ययन।"
                            else
                                "Every minute counts. Daily study target: 13.0 Hours.",
                            color = RudraTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(RudraCyanDark.copy(alpha = 0.5f))
                            .border(1.dp, RudraCyanNeon, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Sun",
                            tint = RudraGold,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // Active / Current Task Card
        item {
            if (currentTask != null) {
                val cat = TaskCategory.fromKey(currentTask.category)
                val catColor = Color(cat.colorHex)
                val subject = Subject.fromKey(currentTask.subjectKey)

                // Calculate remaining minutes in current task
                val endMin = viewModel.timeToMinutes(currentTask.endTime)
                val remainingMin = if (endMin >= currentMinutes) {
                    endMin - currentMinutes
                } else {
                    (endMin + 1440) - currentMinutes
                }

                GlassCard(
                    borderColor = RudraCyanNeon,
                    backgroundColor = RudraNavyCard
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(RudraCyanNeon.copy(alpha = 0.15f))
                                        .border(1.dp, RudraCyanNeon, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "वर्तमान गतिविधि (Active)" else "Current Activity",
                                        color = RudraCyanNeon,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(catColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) cat.labelHi else cat.labelEn,
                                        color = catColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Text(
                                text = "${viewModel.formatTimeString(currentTask.startTime, is24h)} - ${viewModel.formatTimeString(currentTask.endTime, is24h)}",
                                color = RudraGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isHindi) currentTask.titleHi else currentTask.titleEn,
                            color = RudraTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )

                        if (subject != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🎯 " + if (isHindi) subject.nameHi else subject.nameEn,
                                color = Color(subject.colorHex),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Countdown bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(RudraNavyCardElevated)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isHindi) "सत्र समाप्ति में शेष:" else "Time Remaining:",
                                color = RudraTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "$remainingMin " + if (isHindi) "मिनट शेष" else "min left",
                                color = RudraCyanNeon,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (subject != null) {
                                        viewModel.setSelectedSubject(subject)
                                    }
                                    onNavigateToTimer()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("start_timer_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RudraCyanNeon,
                                    contentColor = Color(0xFF001F29)
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "फोकस टाइमर शुरू" else "Start Timer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    if (currentTask.isCompleted) {
                                        viewModel.resetTaskStatus(currentTask.id)
                                    } else {
                                        viewModel.markTaskDone(currentTask.id)
                                    }
                                },
                                modifier = Modifier.testTag("mark_done_button"),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (currentTask.isCompleted) RudraEmerald else RudraNavyCardElevated,
                                    contentColor = if (currentTask.isCompleted) Color.Black else RudraTextPrimary
                                )
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Done")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentTask.isCompleted) {
                                        if (isHindi) "पूर्ण ✓" else "Done ✓"
                                    } else {
                                        if (isHindi) "पूर्ण करें" else "Mark Done"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Next Activity Preview
        if (nextTask != null) {
            item {
                GlassCard(
                    borderColor = RudraNavyBorder,
                    backgroundColor = RudraNavyCardElevated,
                    onClick = onNavigateToTimetable
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(RudraVioletNeon.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Next",
                                    tint = RudraVioletNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = if (isHindi) "अगली गतिविधि (Next Task)" else "Upcoming Task",
                                    color = RudraTextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = if (isHindi) nextTask.titleHi else nextTask.titleEn,
                                    color = RudraTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = viewModel.formatTimeString(nextTask.startTime, is24h),
                                color = RudraGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View",
                                tint = RudraTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daily Progress & Study Target Row
        item {
            GlassCard(
                borderColor = RudraNavyBorder,
                backgroundColor = RudraNavyCard
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "आज की दैनिक प्रगति (Daily Progress)" else "Today's Routine & Study Progress",
                        color = RudraTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CircularProgressMeter(
                            progressPercent = completionPercent,
                            size = 100.dp,
                            strokeWidth = 9.dp,
                            primaryColor = RudraCyanNeon,
                            centerTitle = "${completionPercent.toInt()}%",
                            centerSubtitle = if (isHindi) "दिनचर्या पूर्ण" else "Routine Done"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Target study hours
                            Column {
                                Text(
                                    text = if (isHindi) "नियोजित अध्ययन लक्ष्य" else "Planned Study Target",
                                    color = RudraTextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${totalPlannedStudyMinutes / 60}h (${totalPlannedStudyMinutes}m)",
                                    color = RudraGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Actual completed study hours
                            Column {
                                Text(
                                    text = if (isHindi) "वास्तविक अध्ययन (Focus Log)" else "Actual Focused Study",
                                    color = RudraTextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${completedStudyMinutes / 60}h ${completedStudyMinutes % 60}m",
                                    color = RudraEmerald,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Tasks count
                            Text(
                                text = if (isHindi)
                                    "कार्य: $completedCount / $totalCount पूर्ण"
                                else
                                    "Tasks: $completedCount of $totalCount",
                                color = RudraTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Water Hydration Tracker Card
        item {
            GlassCard(
                borderColor = RudraCyanDark,
                backgroundColor = RudraNavyCardElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RudraCyanDark.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = "Water",
                                tint = RudraCyanNeon,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (isHindi) "जल संतुलन (Water Tracker)" else "Water Intake",
                                color = RudraTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$waterGlasses / 8 " + if (isHindi) "गिलास (लक्ष्य 2.5-3L)" else "Glasses (Goal 2.5L)",
                                color = RudraCyanNeon,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.decrementWater() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(RudraNavyBorder)
                                .testTag("water_minus_button")
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Minus",
                                tint = RudraTextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { viewModel.incrementWater() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(RudraCyanNeon)
                                .testTag("water_plus_button")
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Navigation Buttons Grid
        item {
            Text(
                text = if (isHindi) "त्वरित क्रियाएँ (Quick Navigation)" else "Quick Navigation",
                color = RudraTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickNavCard(
                        title = if (isHindi) "दैनिक दिनचर्या" else "Full Routine",
                        subtitle = if (isHindi) "23 गतिविधियाँ" else "23 Activities",
                        icon = Icons.Default.FormatListNumbered,
                        color = RudraCyanNeon,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_routine_button",
                        onClick = onNavigateToTimetable
                    )

                    QuickNavCard(
                        title = if (isHindi) "फोकस टाइमर" else "Focus Timer",
                        subtitle = if (isHindi) "पॉमोडोरो / 50m" else "Pomodoro / 50m",
                        icon = Icons.Default.Timer,
                        color = RudraVioletNeon,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_timer_button",
                        onClick = onNavigateToTimer
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickNavCard(
                        title = if (isHindi) "प्रातः चेकलिस्ट" else "Morning Routine",
                        subtitle = if (isHindi) "9 नित्यक्रम" else "9 Habits",
                        icon = Icons.Default.WbSunny,
                        color = RudraGold,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_checklist_button",
                        onClick = onNavigateToChecklist
                    )

                    QuickNavCard(
                        title = if (isHindi) "विज्ञान हब" else "Science Skills",
                        subtitle = if (isHindi) "सूत्र व क्विज़" else "Formulas & Quiz",
                        icon = Icons.Default.Science,
                        color = RudraEmerald,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_science_button",
                        onClick = onNavigateToScience
                    )
                }
            }
        }

        // Compact Upcoming List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "आज की आगामी दिनचर्या" else "Today's Schedule Preview",
                    color = RudraTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isHindi) "सभी देखें →" else "View All →",
                    color = RudraCyanNeon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToTimetable() }
                )
            }
        }

        // Top 5 upcoming items
        items(upcomingSlice, key = { it.id }) { task ->
            CompactTaskRow(
                task = task,
                isHindi = isHindi,
                is24h = is24h,
                viewModel = viewModel,
                onToggleDone = {
                    if (task.isCompleted) viewModel.resetTaskStatus(task.id)
                    else viewModel.markTaskDone(task.id)
                }
            )
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.testTag(testTag),
        borderColor = color.copy(alpha = 0.3f),
        backgroundColor = RudraNavyCardElevated,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
                    .border(1.dp, color.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    color = RudraTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = RudraTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun CompactTaskRow(
    task: RoutineTask,
    isHindi: Boolean,
    is24h: Boolean,
    viewModel: RudraViewModel,
    onToggleDone: () -> Unit
) {
    val cat = TaskCategory.fromKey(task.category)
    val catColor = Color(cat.colorHex)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (task.isCompleted) RudraEmerald.copy(alpha = 0.4f) else RudraNavyBorder,
        backgroundColor = RudraNavyCardElevated
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(catColor)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (isHindi) task.titleHi else task.titleEn,
                        color = if (task.isCompleted) RudraTextMuted else RudraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${viewModel.formatTimeString(task.startTime, is24h)} - ${viewModel.formatTimeString(task.endTime, is24h)}",
                        color = RudraGold,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onToggleDone,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Toggle Done",
                    tint = if (task.isCompleted) RudraEmerald else RudraNavyBorder,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
