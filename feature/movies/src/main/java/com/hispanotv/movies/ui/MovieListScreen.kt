package com.hispanotv.movies.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavController
import com.hispanotv.common.ui.component.MovieBackdropCard
import com.hispanotv.common.ui.component.MovieCard
import com.hispanotv.common.ui.theme.HispanoTVTheme
import com.hispanotv.domain.model.Movie

@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val nowPlayingState by viewModel.nowPlayingState.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val query by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Search Bar
            MovieSearchBar(
                query = query,
                onQueryChange = { newQuery ->
                    query = newQuery
                    viewModel.searchMovies(newQuery)
                },
                onClear = {
                    query = ""
                    viewModel.searchMovies("")
                }
            )

            // Content
            when {
                query.isNotBlank() -> {
                    MovieSearchResults(
                        state = searchState,
                        onMovieClick = { movie ->
                            navController.navigate("movie_detail/${movie.id}")
                        },
                        onLoadMore = { viewModel.searchMoreMovies() }
                    )
                }
                else -> {
                    MovieHomeContent(
                        state = nowPlayingState,
                        onMovieClick = { movie ->
                            navController.navigate("movie_detail/${movie.id}")
                        },
                        onLoadMore = { viewModel.loadMoreNowPlaying() }
                    )
                }
            }
        }
    }
}

@Composable
fun MovieSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val textFieldValue = remember { mutableStateOf(query) }

    TextField(
        value = textFieldValue.value,
        onValueChange = { newValue ->
            textFieldValue.value = newValue
            onQueryChange(newValue)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        label = { Text(text = "Buscar películas...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Limpiar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    androidx.compose.foundation.clickable(onClick = {
                        textFieldValue.value = ""
                        onClear()
                    })
                }
            }
        },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        ),
        keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
            onSearch = { onQueryChange(textFieldValue.value) }
        ),
        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun MovieHomeContent(
    state: MovieViewModel.MovieListUiState,
    onMovieClick: (Movie) -> Unit,
    onLoadMore: () -> Unit
) {
    when (state) {
        is MovieViewModel.MovieListUiState.Loading -> LoadingScreen()
        is MovieViewModel.MovieListUiState.Error -> ErrorScreen(message = state.message, onRetry = { /* TODO */ })
        is MovieViewModel.MovieListUiState.Success -> {
            val movies = state.movies
            val currentPage = state.currentPage
            val totalPages = state.totalPages

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Featured movie (first one)
                if (movies.isNotEmpty()) {
                    MovieBackdropCard(
                        movie = movies[0],
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onMovieClick(movies[0]) }
                    )
                }

                // Now Playing section
                SectionHeader(
                    title = "Estrenos en cines",
                    seeAllAction = { /* TODO: Navigate to full list */ }
                )

                MovieGrid(
                    movies = if (movies.size > 1) movies.drop(1) else emptyList(),
                    onMovieClick = onMovieClick,
                    onLoadMore = if (currentPage < totalPages) onLoadMore else null
                )
            }
        }
        is MovieViewModel.MovieListUiState.Idle -> EmptyState()
    }
}

@Composable
fun MovieSearchResults(
    state: MovieViewModel.MovieListUiState,
    onMovieClick: (Movie) -> Unit,
    onLoadMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (state) {
            is MovieViewModel.MovieListUiState.Loading -> LoadingScreen()
            is MovieViewModel.MovieListUiState.Error -> ErrorScreen(message = state.message, onRetry = { /* TODO */ })
            is MovieViewModel.MovieListUiState.Success -> {
                val movies = state.movies
                val currentPage = state.currentPage
                val totalPages = state.totalPages

                if (movies.isEmpty()) {
                    EmptyState(
                        message = "No se encontraron películas",
                        icon = Icons.Filled.SearchOff
                    )
                } else {
                    SectionHeader(title = "Resultados (${movies.size})")

                    MovieGrid(
                        movies = movies,
                        onMovieClick = onMovieClick,
                        onLoadMore = if (currentPage < totalPages) onLoadMore else null
                    )
                }
            }
            is MovieViewModel.MovieListUiState.Idle -> EmptyState()
        }
    }
}

@Composable
fun MovieGrid(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onLoadMore: (() -> Unit)? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(movies) { movie ->
            MovieCard(
                movie = movie,
                modifier = Modifier.width(180.dp),
                onClick = { onMovieClick(movie) },
                onFavoriteClick = { /* TODO */ },
                onWatchlistClick = { /* TODO */ }
            )
        }

        // Load more trigger
        if (onLoadMore != null) {
            item {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onLoadMore,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Cargar más")
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    seeAllAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (seeAllAction != null) {
            androidx.compose.material3.TextButton(onClick = seeAllAction) {
                Text(text = "Ver todo")
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            androidx.compose.material3.FilledButton(onClick = onRetry) {
                Text(text = "Reintentar")
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String = "No hay contenido disponible",
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Filled.Movie
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}