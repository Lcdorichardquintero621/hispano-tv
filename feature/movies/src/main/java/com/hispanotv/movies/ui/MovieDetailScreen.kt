package com.hispanotv.movies.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hispanotv.common.ui.component.MovieCard
import com.hispanotv.common.ui.theme.HispanoTVTheme
import com.hispanotv.domain.model.Movie
import com.hispanotv.domain.model.MovieVideo

@Composable
fun MovieDetailScreen(
    navController: NavController,
    viewModel: MovieViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val movieId = navController.getString("movieId") ?: ""
    val detailsState by viewModel.movieDetailsState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(movieId) {
        if (movieId.isNotEmpty()) {
            viewModel.loadMovieDetails(movieId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (detailsState) {
            is MovieViewModel.MovieDetailsUiState.Loading -> LoadingScreen()
            is MovieViewModel.MovieDetailsUiState.Error -> ErrorScreen(message = detailsState.message, onRetry = { viewModel.loadMovieDetails(movieId) })
            is MovieViewModel.MovieDetailsUiState.Success -> MovieDetailContent(
                movie = detailsState.movie,
                onBackClick = { navController.popBackStack() },
                onFavoriteClick = { viewModel.toggleFavorite(movieId) },
                onWatchlistClick = { viewModel.toggleWatchlist(movieId) }
            )
            is MovieViewModel.MovieDetailsUiState.Idle -> LoadingScreen()
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: Movie,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp)
    ) {
        // Backdrop + Poster
        item {
            MovieDetailHeader(
                movie = movie,
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick,
                onWatchlistClick = onWatchlistClick
            )
        }

        // Info Section
        item {
            MovieInfoSection(movie = movie)
        }

        // Genres
        item {
            GenresChips(genres = movie.genres)
        }

        // Overview
        item {
            OverviewSection(overview = movie.overview)
        }

        // Details
        item {
            DetailsSection(movie = movie)
        }

        // Cast/Crew (placeholder)
        item {
            CastSection()
        }

        // Similar movies (placeholder)
        item {
            SimilarMoviesSection()
        }
    }
}

@Composable
fun MovieDetailHeader(
    movie: Movie,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Backdrop
        if (movie.backdropUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.backdropUrl!!)
                    .crossfade(true)
                    .build(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Gradient overlay
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                    )
                ),
                size = size
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Back button
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Poster + Title
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (movie.posterUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(movie.posterUrl!!)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = movie.title,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .height(150.dp)
                                    .width(100.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                    .shadow(4.dp, androidx.compose.ui.graphics.Shape.RoundedCornerShape(8.dp))
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = movie.title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Rating
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp, 8.dp)
                                        .background(
                                            color = ratingColor(movie.voteAverage),
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${movie.ratingPercentage}%",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                // Year
                                Text(
                                    text = movie.year,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )

                                // Runtime
                                movie.runtime?.let { runtime ->
                                    Text(
                                        text = "${runtime} min",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // Genres
                            Text(
                                text = movie.formattedGenres,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Actions
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    androidx.compose.material3.IconButton(onClick = onWatchlistClick) {
                        Icon(
                            imageVector = if (movie.isInWatchlist)
                                androidx.compose.material.icons.filled.Bookmark
                            else
                                androidx.compose.material.icons.outlined.Bookmark,
                            contentDescription = if (movie.isInWatchlist) "En mi lista" : "Añadir a mi lista",
                            tint = if (movie.isInWatchlist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                    androidx.compose.material3.IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (movie.isFavorite)
                                androidx.compose.material.icons.filled.Favorite
                            else
                                androidx.compose.material.icons.outlined.Favorite,
                            contentDescription = if (movie.isFavorite) "Favorito" : "Añadir a favoritos",
                            tint = if (movie.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieInfoSection(movie: Movie) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Información",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoItem(label = "Idioma original", value = movie.originalLanguage.uppercase())
            InfoItem(label = "Estreno", value = movie.releaseDate)
            InfoItem(label = "Duración", value = movie.runtime?.let { "${it} min" } ?: "N/A")
            InfoItem(label = "Estado", value = movie.status.name)
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun GenresChips(genres: List<com.hispanotv.domain.model.Genre>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Géneros",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genres.forEach { genre ->
                androidx.compose.material3.Chip(
                    onClick = { /* Filter by genre */ },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(text = genre.name)
                }
            }
        }
    }
}

@Composable
fun OverviewSection(overview: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Sinopsis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = overview,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun DetailsSection(movie: Movie) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Detalles",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        DetailRow("Presupuesto", movie.budget?.let { formatCurrency(it) } ?: "N/A")
        DetailRow("Recaudación", movie.revenue?.let { formatCurrency(it) } ?: "N/A")

        if (movie.productionCompanies.isNotEmpty()) {
            DetailRow(
                "Productoras",
                movie.productionCompanies.take(3).joinToString(", ") { it.name }
            )
        }

        if (movie.productionCountries.isNotEmpty()) {
            DetailRow(
                "Países",
                movie.productionCountries.joinToString(", ") { it.name }
            )
        }

        if (movie.spokenLanguages.isNotEmpty()) {
            DetailRow(
                "Idiomas",
                movie.spokenLanguages.joinToString(", ") { it.name }
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CastSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Reparto",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Información de reparto no disponible en esta versión",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SimilarMoviesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Películas similares",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Películas similares no disponibles en esta versión",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
                imageVector = androidx.compose.material.icons.filled.ErrorOutline,
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

private fun ratingColor(rating: Double): Color {
    return when {
        rating >= 7.5 -> Color(0xFF4CAF50)
        rating >= 6.0 -> Color(0xFFFF9800)
        rating >= 4.0 -> Color(0xFFFF5722)
        else -> Color(0xFFF44336)
    }
}

private fun formatCurrency(amount: Long): String {
    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US).format(amount)
}