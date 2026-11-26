package com.hispanotv.data.source

import com.hispanotv.data.api.TmdbApi
import com.hispanotv.data.local.dao.MovieDao
import com.hispanotv.data.local.entity.MovieEntity
import com.hispanotv.domain.model.Genre
import com.hispanotv.domain.model.Movie
import com.hispanotv.domain.model.MovieListResponse
import com.hispanotv.domain.model.MovieVideo
import com.hispanotv.domain.model.WatchProviders
import com.hispanotv.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TmdbRepository @Inject constructor(
    private val api: TmdbApi,
    private val movieDao: MovieDao,
    private val apiKey: String
) : MovieRepository {

    override suspend fun getNowPlaying(page: Int): Result<MovieListResponse> = safeCall {
        api.getNowPlaying(apiKey, page = page)
    }

    override suspend fun getPopular(page: Int): Result<MovieListResponse> = safeCall {
        api.getPopular(apiKey, page = page)
    }

    override suspend fun getTopRated(page: Int): Result<MovieListResponse> = safeCall {
        api.getTopRated(apiKey, page = page)
    }

    override suspend fun getUpcoming(page: Int): Result<MovieListResponse> = safeCall {
        api.getUpcoming(apiKey, page = page)
    }

    override suspend fun searchMovies(query: String, page: Int): Result<MovieListResponse> = safeCall {
        api.searchMovies(apiKey, query, page = page)
    }

    override suspend fun getMovieDetails(movieId: String): Result<Movie> = safeCall {
        val response = api.getMovieDetails(movieId.toInt(), apiKey)
        response.toMovie()
    }

    override suspend fun getMovieVideos(movieId: String): Result<List<MovieVideo>> = safeCall {
        val response = api.getMovieVideos(movieId.toInt(), apiKey)
        response.results
    }

    override suspend fun getWatchProviders(movieId: String): Result<WatchProviders> = safeCall {
        val response = api.getWatchProviders(movieId.toInt(), apiKey)
        response.toWatchProviders()
    }

    override suspend fun getSimilarMovies(movieId: String, page: Int): Result<MovieListResponse> = safeCall {
        api.getSimilarMovies(movieId.toInt(), apiKey, page = page)
    }

    override suspend fun getRecommendations(movieId: String, page: Int): Result<MovieListResponse> = safeCall {
        api.getRecommendations(movieId.toInt(), apiKey, page = page)
    }

    override suspend fun getGenres(): Result<List<Genre>> = safeCall {
        val response = api.getGenres(apiKey)
        response.genres
    }

    override suspend fun toggleFavorite(movieId: String): Result<Boolean> {
        return try {
            val movie = movieDao.getMovieById(movieId)
                ?: throw IllegalArgumentException("Película no encontrada")
            val newFavorite = !movie.isFavorite
            withContext(Dispatchers.IO) {
                movieDao.updateFavorite(movieId, newFavorite)
            }
            Result.Success(newFavorite)
        } catch (e: Exception) {
            Result.Error(e, "Error al actualizar favorito")
        }
    }

    override suspend fun toggleWatchlist(movieId: String): Result<Boolean> {
        return try {
            val movie = movieDao.getMovieById(movieId)
                ?: throw IllegalArgumentException("Película no encontrada")
            val newWatchlist = !movie.isInWatchlist
            withContext(Dispatchers.IO) {
                movieDao.updateWatchlist(movieId, newWatchlist)
            }
            Result.Success(newWatchlist)
        } catch (e: Exception) {
            Result.Error(e, "Error al actualizar watchlist")
        }
    }

    override suspend fun getFavoriteMovies(): Result<List<Movie>> {
        return try {
            val movies = movieDao.getFavoriteMovies().first()
            Result.Success(movies.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener favoritos")
        }
    }

    override suspend fun getWatchlistMovies(): Result<List<Movie>> {
        return try {
            val movies = movieDao.getWatchlistMovies().first()
            Result.Success(movies.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener watchlist")
        }
    }

    override val favoriteMoviesFlow: Flow<List<Movie>> = movieDao.getFavoriteMovies().map {
        it.map { it.toDomain() }
    }

    override val watchlistMoviesFlow: Flow<List<Movie>> = movieDao.getWatchlistMovies().map {
        it.map { it.toDomain() }
    }

    suspend fun cacheMovies(response: MovieListResponse) {
        val entities = response.results.map { MovieEntity.fromDomain(it) }
        withContext(Dispatchers.IO) {
            movieDao.insertAll(entities)
        }
    }

    private inline fun <T> safeCall(crossinline block: () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: Exception) {
            Result.Error(e, "Error de red: ${e.message}")
        }
    }
}