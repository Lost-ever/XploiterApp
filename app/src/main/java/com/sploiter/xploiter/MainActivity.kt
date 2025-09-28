package com.sploiter.xploiter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.sploiter.xploiter.ui.theme.ExploiterTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Exploit : Screen("exploit")
    object Payloads : Screen("payloads")
    object Scanning : Screen("scanning")
    object Settings : Screen("settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExploiterTheme {
                Home()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Home() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var isLoading by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val screens = listOf(Screen.Home, Screen.Profile, Screen.Exploit,
        Screen.Payloads, Screen.Scanning, Screen.Settings)
    val currentScreenName = currentRoute.replaceFirstChar { it.uppercase() }
    val targetRotation = if (drawerState.isOpen || drawerState.isAnimationRunning) {
        -180f
    } else {
        0f
    }

    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "MenuIconRotation"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.7f)
            ) {
                Text(
                    "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider(Modifier.padding(vertical = 5.dp))

                screens.forEach { screen ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Home -> Icons.Default.Home
                                    Screen.Profile -> Icons.Default.Person
                                    Screen.Exploit -> Icons.Default.Handyman
                                    Screen.Payloads -> Icons.Default.Storage
                                    Screen.Scanning -> Icons.Default.Scanner
                                    Screen.Settings -> Icons.Default.Settings
                                },
                                contentDescription = null
                            )
                        },
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                isLoading = true
                                delay(400)
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                    popUpTo(Screen.Home.route) { saveState = true }
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreenName)},
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed){ drawerState.open() } else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.rotate(rotation))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    )
                )
            },
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                    if (isLoading) {
                        LoadingScreenContent()
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
                            composable(Screen.Home.route) { HomeScreenContent() }
                            composable(Screen.Profile.route) { ProfileScreenContent() }
                            composable(Screen.Exploit.route) { ExploitScreenContent() }
                            composable(Screen.Payloads.route) { PayloadsScreenContent() }
                            composable(Screen.Scanning.route) { ScanningScreenContent() }
                            composable(Screen.Settings.route) { SettingsScreenContent() }
                        }
                    }
                }
            }
        )
    }
}
@Composable
fun LoadingScreenContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Loading...")
    }
}
@Composable
fun HomeScreenContent() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Home Dashboard", style = MaterialTheme.typography.headlineSmall)
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Text("Welcome to the main console. Select a module from the menu.")
    }
}

@Composable
fun ProfileScreenContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile Fragment Content: User Account Details.")
    }
}

@Composable
fun ExploitScreenContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Exploit Fragment Content: Live Attack Editor.")
    }
}

@Composable
fun PayloadsScreenContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Payloads Fragment Content: Storage Management.")
    }
}

@Composable
fun ScanningScreenContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Scanning Fragment Content: Network Discovery Tools.")
    }
}

@Composable
fun SettingsScreenContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings Fragment Content: Application Configuration.")
    }
}