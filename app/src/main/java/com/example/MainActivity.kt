package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.HeaderISTClock
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusTimerScreen
import com.example.ui.screens.MorningChecklistScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.ScienceSkillsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RudraCyanNeon
import com.example.ui.theme.RudraGold
import com.example.ui.theme.RudraNavyBorder
import com.example.ui.theme.RudraNavyCard
import com.example.ui.theme.RudraNavyCardElevated
import com.example.ui.theme.RudraNavyDarkest
import com.example.ui.theme.RudraTextMuted
import com.example.ui.theme.RudraTextSecondary
import com.example.ui.viewmodel.RudraViewModel

enum class RudraScreen(
    val route: String,
    val titleHi: String,
    val titleEn: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "डैशबोर्ड", "Dashboard", Icons.Default.Dashboard),
    TIMETABLE("timetable", "दिनचर्या", "Routine", Icons.Default.FormatListNumbered),
    TIMER("timer", "टाइमर", "Timer", Icons.Default.Timer),
    MORNING("morning", "प्रातः", "Morning", Icons.Default.WbSunny),
    SCIENCE("science", "विज्ञान", "Science", Icons.Default.Science),
    REPORTS("reports", "प्रगति", "Reports", Icons.Default.Assessment),
    SETTINGS("settings", "सेटिंग्स", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RudraApp()
            }
        }
    }
}

@Composable
fun RudraApp(viewModel: RudraViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(RudraScreen.DASHBOARD) }

    val istTime by viewModel.istTimeFormatted.collectAsState()
    val istDate by viewModel.istDateFormatted.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val is24h by viewModel.is24HourFormat.collectAsState()

    // Handle back button to return to dashboard
    BackHandler(enabled = currentScreen != RudraScreen.DASHBOARD) {
        currentScreen = RudraScreen.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = RudraNavyDarkest,
        topBar = {
            HeaderISTClock(
                timeFormatted = istTime,
                dateFormatted = istDate,
                isHindi = isHindi,
                is24Hour = is24h,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onToggle24Hour = { viewModel.toggleTimeFormat() }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar"),
                containerColor = RudraNavyCard,
                tonalElevation = 8.dp
            ) {
                RudraScreen.entries.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = if (isHindi) screen.titleHi else screen.titleEn,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isHindi) screen.titleHi else screen.titleEn,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = RudraCyanNeon,
                            indicatorColor = RudraCyanNeon,
                            unselectedIconColor = RudraTextMuted,
                            unselectedTextColor = RudraTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(RudraNavyDarkest)
        ) {
            when (currentScreen) {
                RudraScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTimetable = { currentScreen = RudraScreen.TIMETABLE },
                    onNavigateToTimer = { currentScreen = RudraScreen.TIMER },
                    onNavigateToChecklist = { currentScreen = RudraScreen.MORNING },
                    onNavigateToScience = { currentScreen = RudraScreen.SCIENCE },
                    onNavigateToReports = { currentScreen = RudraScreen.REPORTS }
                )
                RudraScreen.TIMETABLE -> TimetableScreen(viewModel = viewModel)
                RudraScreen.TIMER -> FocusTimerScreen(viewModel = viewModel)
                RudraScreen.MORNING -> MorningChecklistScreen(viewModel = viewModel)
                RudraScreen.SCIENCE -> ScienceSkillsScreen(
                    viewModel = viewModel,
                    onStartScienceTimer = { currentScreen = RudraScreen.TIMER }
                )
                RudraScreen.REPORTS -> ReportsScreen(viewModel = viewModel)
                RudraScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
