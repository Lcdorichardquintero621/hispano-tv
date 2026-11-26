package com.hispanotv.domain.repository

import com.hispanotv.domain.model.Movie
import com.hispanotv.domain.model.MovieListResponse
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getNowPlaying(page: Int): Result<MovieListResponse>
    suspend fun getPopular(page: Int): Result<MovieListResponse>
    suspend fun getTopRated(page: Int): Result<MovieListResponse>
    suspend fun getUpcoming(page: Int): Result<MovieListResponse>
    suspend fun searchMovies(query: String, page: Int): Result<MovieListResponse>
    suspend fun getMovieDetails(movieId: String): Result<Movie>
    suspend fun getMovieVideos(movieId: String): Result<List<com.hispanotv.domain.model.MovieVideo>>
    suspend fun getWatchProviders(movieId: String): Result<com.hispanotv.domain.model.WatchProviders>
    suspend fun getSimilarMovies(movieId: String, page: Int): Result<MovieListResponse>
    suspend fun getRecommendations(movieId: String, page: Int): Result<MovieListResponse>
    suspend fun getGenres(): Result<List<com.hispanotv.domain.model.Genre>>

    suspend fun toggleFavorite(movieId: String): Result<Boolean>
    suspend fun toggleWatchlist(movieId: String): Result<Boolean>
    suspend fun getFavoriteMovies(): Result<List<Movie>>
    suspend fun getWatchlistMovies(): Result<List<Movie>>

    // Flow for reactive UI
    val favoriteMoviesFlow: Flow<List<Movie>>
    val watchlistMoviesFlow: Flow<List<Movie>>

    sealed interface Result<out T> {
        data class Success<out T>(val data: T) : Result<T>
        data class Error(val throwable: Throwable, val userMessage: String) : Result<Nothing>
    }
}