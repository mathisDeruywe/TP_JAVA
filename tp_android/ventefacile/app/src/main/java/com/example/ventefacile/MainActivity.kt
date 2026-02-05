package com.example.ventefacile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ventefacile.ui.AuthScreen
import com.example.ventefacile.ui.HistoryScreen
import com.example.ventefacile.ui.VenteScreen
import com.example.ventefacile.ui.theme.VentefacileTheme
import com.example.ventefacile.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VentefacileTheme {
                if (viewModel.currentUser == null) {
                    AuthScreen(viewModel = viewModel, onAuthSuccess = {
                        viewModel.chargerHistorique()
                    })
                } else {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = { Text("VenteFacile") },
                                actions = {
                                    IconButton(onClick = { viewModel.logout() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = "Déconnexion"
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.PointOfSale, contentDescription = null) },
                                    label = { Text("Vente") },
                                    selected = currentRoute == "vente",
                                    onClick = {
                                        navController.navigate("vente") {
                                            popUpTo("vente") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                                    label = { Text("Historique") },
                                    selected = currentRoute == "historique",
                                    onClick = {
                                        navController.navigate("historique") {
                                            popUpTo("vente") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "vente",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("vente") {
                                VenteScreen(viewModel = viewModel)
                            }
                            composable("historique") {
                                HistoryScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}