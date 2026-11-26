package com.hispanotv.domain.usecase.movie

import com.hispanotv.domain.model.MovieListResponse
import com.hispanotv.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetNowPlayingMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(page: Int = 1): Flow<MovieRepository.Result<MovieListResponse>> =
        movieRepository.getNowPlaying(page).asFlow()
            .catch { e ->
                emit(MovieRepository.Result.Error(e, "Error al cargar estrenos"))
            }
}