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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TmdbRepository @Inject constructor(
    private val api: TmdbApi,
    private val movieDao: MovieDao,
    private val apiKey: String
) : MovieRepository {

    override suspend fun getNowPlaying(page: Int): MovieRepository.Result<MovieListResponse> = safeCall {
        api.getNowPlaying(apiKey, page = page)
    }

    override suspend fun getPopular(page: Int): MovieRepository.Result<MovieListResponse> = safeCall {
        api.getPopular(apiKey, page = page)
    }

    override suspend fun getTopRated(page: Int): MovieRepository.Result<MovieListResponse> = safeCall {
        api.getTopRated(apiKey, page = page)
    }

    override suspend fun getUpcoming(page: Int): MovieRepository.Result<MovieListResponse> = safeCall {
        api.getUpcoming(apiKey, page = page)
    }

    override suspend fun searchMovies(query: String, page: Int): MovieRepository.Result<MovieListResponse> = safeCall {
        api.searchMovies(apiKey, query, page = page)
    }

    override suspend fun getMovieDetails(movieId: String): MovieRepository.Result<Movie> = safeCall {
        val response = api.getMovieDetails(movieId.toInt(), apiKey)
        response.toMovie()
    }

    override suspend fun getMovieVideos(movieId: String): MovieRepository.Result<List<MovieVideo>> = safeCall {
        val response = api.getMovieVideos(movieId.toInt(), apiKey)
        response.results
    }

    override suspend fun getWatchProviders(movieId: String): MovieRepository.Result<WatchProviders> = safeCall {
        val response = api.getWatchProviders(movieId.toInt(), apiKey)
        response.toWatchProviders()
    }

    override suspend fun getSimilarMovies(movieId: String, page: Int): MovieRepository.Result<MovieListResponse> = safeCall {
        api.getSimilarMovies(movieId.toInt(), apiKey, page = page)
    }

    override suspend fun getRecommendations(movieId: String, page: Int): MovieRepository.Result<MovieListResponse> = safeCall {
        api.getRecommendations(movieId.toInt(), apiKey, page = page)
    }

    override suspend fun getGenres(): MovieRepository.Result<List<Genre>> = safeCall {
        val response = api.getGenres(apiKey)
        response.genres
    }

    override suspend fun toggleFavorite(movieId: String): MovieRepository.Result<Boolean> {
        return try {
            val movie = movieDao.getMovieById(movieId)
                ?: throw IllegalArgumentException("Película no encontrada")
            val newFavorite = !movie.isFavorite
            withContext(Dispatchers.IO) {
                movieDao.updateFavorite(movieId, newFavorite)
            }
            MovieRepository.Result.Success(newFavorite)
        } catch (e: Exception) {
            MovieRepository.Result.Error(e, "Error al actualizar favorito")
        }
    }

    override suspend fun toggleWatchlist(movieId: String): MovieRepository.Result<Boolean> {
        return try {
            val movie = movieDao.getMovieById(movieId)
                ?: throw IllegalArgumentException("Película no encontrada")
            val newWatchlist = !movie.isInWatchlist
            withContext(Dispatchers.IO) {
                movieDao.updateWatchlist(movieId, newWatchlist)
            }
            MovieRepository.Result.Success(newWatchlist)
        } catch (e: Exception) {
            MovieRepository.Result.Error(e, "Error al actualizar watchlist")
        }
    }

    override suspend fun getFavoriteMovies(): MovieRepository.Result<List<Movie>> {
        return try {
            val movies = movieDao.getFavoriteMovies().first()
            MovieRepository.Result.Success(movies.map { it.toDomain() })
        } catch (e: Exception) {
            MovieRepository.Result.Error(e, "Error al obtener favoritos")
        }
    }

    override suspend fun getWatchlistMovies(): MovieRepository.Result<List<Movie>> {
        return try {
            val movies = movieDao.getWatchlistMovies().first()
            MovieRepository.Result.Success(movies.map { it.toDomain() })
        } catch (e: Exception) {
            MovieRepository.Result.Error(e, "Error al obtener watchlist")
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

    private suspend fun <T> safeCall(block: suspend () -> T): MovieRepository.Result<T> {
        return try {
            MovieRepository.Result.Success(block())
        } catch (e: Exception) {
            MovieRepository.Result.Error(e, "Error de red: ${e.message}")
        }
    }
}