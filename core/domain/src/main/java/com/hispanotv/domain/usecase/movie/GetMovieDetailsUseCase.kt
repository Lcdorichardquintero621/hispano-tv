package com.hispanotv.domain.usecase.movie

import com.hispanotv.domain.model.Movie
import com.hispanotv.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(movieId: String): Flow<MovieRepository.Result<Movie>> =
        movieRepository.getMovieDetails(movieId).asFlow()
            .catch { e ->
                emit(MovieRepository.Result.Error(e, "Error al cargar detalles"))
            }
}