package com.hispanotv.domain.usecase.movie

import com.hispanotv.domain.model.MovieListResponse
import com.hispanotv.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class SearchMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(query: String, page: Int = 1): Flow<MovieRepository.Result<MovieListResponse>> =
        if (query.isBlank()) {
            flowOf(MovieRepository.Result.Success(MovieListResponse(1, emptyList(), 0, 0)))
        } else {
            movieRepository.searchMovies(query, page).asFlow()
                .catch { e ->
                    emit(MovieRepository.Result.Error(e, "Error en la búsqueda"))
                }
        }
}