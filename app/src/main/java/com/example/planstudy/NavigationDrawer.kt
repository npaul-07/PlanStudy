package com.example.planstudy

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planstudy.ui.calendar.CalendarScreen
import com.example.planstudy.ui.screens.Books.BookRecommendationScreen
import com.example.planstudy.ui.screens.home.HomeScreen
import com.example.planstudy.ui.screens.pomodoro.PomodoroScreen
import com.example.planstudy.ui.screens.tasks.TaskScreen
import kotlinx.coroutines.launch

data class screens(
    val title: String,
    val icon: ImageVector
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedItem = remember { mutableStateOf("Home") }
    val drawerItems = listOf(
        screens("Home", Icons.Default.Home),
        screens("Tasks", Icons.Default.Info),
        screens("Pomodoro", Icons.Filled.Refresh), // Add this
        screens("Calendar", Icons.Default.DateRange),
        screens("Other Books", Icons.Default.List)
    )
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),

                    ) {
                    Text(
                        text = "Study Planner",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    drawerItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(text = item.title) },
                            selected = item.title == selectedItem.value,
                            onClick = {
                                selectedItem.value = item.title
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            modifier = Modifier.padding(vertical = 8.dp),
                            icon = {
                                Icon(imageVector = item.icon, contentDescription = item.title)
                            },

                            )
                    }
                }

            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = selectedItem.value)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                when (selectedItem.value) {
                    "Home" -> HomeScreen(modifier = Modifier.padding(contentPadding))
                    "Tasks" -> TaskScreen()
                    "Pomodoro" -> PomodoroScreen(modifier = Modifier.padding(contentPadding))
                    "Calendar" -> CalendarScreen()
                    "Other Books" -> BookRecommendationScreen()
                }
            }
        }
    }
}
