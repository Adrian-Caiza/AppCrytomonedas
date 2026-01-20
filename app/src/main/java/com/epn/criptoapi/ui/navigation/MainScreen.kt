package com.epn.criptoapi.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.epn.criptoapi.ui.screens.CryptoDetailScreen
import com.epn.criptoapi.ui.screens.CryptoListScreen
import com.epn.criptoapi.ui.screens.FavoritesScreen
import com.epn.criptoapi.ui.screens.TrendingScreen
import com.epn.criptoapi.ui.viewmodel.CryptoDetailViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Mercado", Icons.Default.Home)
    object Trending : Screen("trending", "Trending", Icons.Default.Star)
    object Favorites : Screen("favorites", "Favoritos", Icons.Default.Favorite)
    object Detail : Screen("detail/{cryptoId}", "Detalle", Icons.Default.Home)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Solo mostramos la barra en las pantallas principales
            if (currentRoute == Screen.Home.route ||
                currentRoute == Screen.Trending.route ||
                currentRoute == Screen.Favorites.route) {

                NavigationBar {
                    val items = listOf(Screen.Home, Screen.Trending, Screen.Favorites)
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                CryptoListScreen(
                    onCryptoClick = { cryptoId ->
                        navController.navigate("detail/$cryptoId")
                    }
                )
            }

            composable(Screen.Trending.route) {
                TrendingScreen(
                    onCryptoClick = { cryptoId ->
                        navController.navigate("detail/$cryptoId")
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onCryptoClick = { cryptoId ->
                        navController.navigate("detail/$cryptoId")
                    }
                )
            }

            composable(Screen.Detail.route) { backStackEntry ->
                val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: return@composable
                // Usamos viewModel() sin factory compleja ya que AndroidViewModel tiene factory por defecto
                val viewModel: CryptoDetailViewModel = viewModel()

                CryptoDetailScreen(
                    cryptoId = cryptoId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}