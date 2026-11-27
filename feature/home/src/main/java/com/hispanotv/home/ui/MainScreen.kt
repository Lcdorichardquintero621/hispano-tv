package com.hispanotv.home.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.hispanotv.common.ui.theme.HispanoTVTheme
import com.hispanotv.movies.ui.MovieDetailScreen
import com.hispanotv.movies.ui.MovieListScreen
import com.hispanotv.tv.ui.channel.ChannelGridScreen
import com.hispanotv.tv.ui.country.CountryListScreen
import com.hispanotv.tv.ui.player.PlayerScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeContent(
                navController = navController,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
        composable(
            route = "channels/{countryCode}",
            arguments = listOf(navArgument("countryCode") { type = androidx.navigation.NavType.StringType })
        ) {
            ChannelGridScreen(navController = navController)
        }
        composable(
            route = "player/{channelId}",
            arguments = listOf(navArgument("channelId") { type = androidx.navigation.NavType.StringType })
        ) {
            PlayerScreen(navController = navController)
        }
        composable(route = "movies") {
            MovieListScreen(navController = navController)
        }
        composable(
            route = "movie_detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = androidx.navigation.NavType.StringType })
        ) {
            MovieDetailScreen(navController = navController)
        }
    }
}

@Composable
fun HomeContent(
    navController: NavController,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Main content based on selected tab
        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            when (selectedTab) {
                0 -> CountryListScreen(navController = navController)
                1 -> MovieListScreen(navController = navController)
                2 -> FavoritesScreen()
                3 -> SettingsScreen()
            }
        }

        // Bottom Navigation Bar
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            val items = listOf(
                NavigationItem("TV en Vivo", Icons.Filled.LiveTv, Icons.Outlined.LiveTv),
                NavigationItem("Películas", Icons.Filled.Movie, Icons.Outlined.Movie),
                NavigationItem("Favoritos", Icons.Filled.Favorite, Icons.Outlined.Favorite),
                NavigationItem("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings)
            )

            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == index) item.filled else item.outlined,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(text = item.label, fontSize = 12.sp) },
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    alwaysShowLabel = true
                )
            }
        }
    }
}

@Composable
fun FavoritesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = "",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tus favoritos aparecerán aquí",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Ajustes - Próximamente",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class NavigationItem(
    val label: String,
    val filled: androidx.compose.ui.graphics.vector.ImageVector,
    val outlined: androidx.compose.ui.graphics.vector.ImageVector
)