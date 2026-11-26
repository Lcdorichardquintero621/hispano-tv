package com.hispanotv.movies.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hispanotv.domain.model.Movie
import com.hispanotv.domain.model.MovieListResponse
import com.hispanotv.domain.repository.MovieRepository
import com.hispanotv.domain.usecase.movie.GetNowPlayingMoviesUseCase
import com.hispanotv.domain.usecase.movie.SearchMoviesUseCase
import com.hispanotv.domain.usecase.movie.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getNowPlayingUseCase: GetNowPlayingMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _nowPlayingState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val nowPlayingState: StateFlow<MovieListUiState> = _nowPlayingState

    private val _searchState = MutableStateFlow<MovieListUiState>(MovieListUiState.Idle)
    val searchState: StateFlow<MovieListUiState> = _searchState

    private val _movieDetailsState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Idle)
    val movieDetailsState: StateFlow<MovieDetailsUiState> = _movieDetailsState

    private var currentPage = 1
    private var currentQuery = ""
    private var allMovies = mutableListOf<Movie>()

    fun loadNowPlaying() {
        currentPage = 1
        allMovies.clear()
        loadNowPlayingPage(currentPage)
    }

    fun loadMoreNowPlaying() {
        currentPage++
        loadNowPlayingPage(currentPage)
    }

    private fun loadNowPlayingPage(page: Int) {
        viewModelScope.launch {
            if (page == 1) {
                _nowPlayingState.value = MovieListUiState.Loading
            }
            getNowPlayingUseCase(page).collect { result ->
                _nowPlayingState.value = when (result) {
                    is MovieRepository.Result.Success -> {
                        val newMovies = result.data.results
                        if (page == 1) {
                            allMovies = newMovies.toMutableList()
                        } else {
                            allMovies.addAll(newMovies)
                        }
                        MovieListUiState.Success(allMovies.toList(), result.data.page, result.data.totalPages)
                    }
                    is MovieRepository.Result.Error -> MovieListUiState.Error(result.userMessage)
                }
            }
        }
    }

    fun searchMovies(query: String) {
        currentQuery = query
        currentPage = 1
        if (query.isBlank()) {
            _searchState.value = MovieListUiState.Idle
            return
        }
        searchMoviesPage(currentPage)
    }

    fun searchMoreMovies() {
        currentPage++
        searchMoviesPage(currentPage)
    }

    private fun searchMoviesPage(page: Int) {
        viewModelScope.launch {
            if (page == 1) {
                _searchState.value = MovieListUiState.Loading
            }
            searchMoviesUseCase(currentQuery, page).collect { result ->
                _searchState.value = when (result) {
                    is MovieRepository.Result.Success -> {
                        val newMovies = result.data.results
                        if (page == 1) {
                            allMovies = newMovies.toMutableList()
                        } else {
                            allMovies.addAll(newMovies)
                        }
                        MovieListUiState.Success(allMovies.toList(), result.data.page, result.data.totalPages)
                    }
                    is MovieRepository.Result.Error -> MovieListUiState.Error(result.userMessage)
                }
            }
        }
    }

    fun loadMovieDetails(movieId: String) {
        viewModelScope.launch {
            _movieDetailsState.value = MovieDetailsUiState.Loading
            getMovieDetailsUseCase(movieId).collect { result ->
                _movieDetailsState.value = when (result) {
                    is MovieRepository.Result.Success -> MovieDetailsUiState.Success(result.data)
                    is MovieRepository.Result.Error -> MovieDetailsUiState.Error(result.userMessage)
                }
            }
        }
    }

    fun toggleFavorite(movieId: String) {
        // TODO: Implement
    }

    fun toggleWatchlist(movieId: String) {
        // TODO: Implement
    }

    sealed interface MovieListUiState {
        data class Loading : MovieListUiState
        data class Success(val movies: List<Movie>, val currentPage: Int, val totalPages: Int) : MovieListUiState
        data class Error(val message: String) : MovieListUiState
        data class Idle : MovieListUiState
    }

    sealed interface MovieDetailsUiState {
        data class Loading : MovieDetailsUiState
        data class Success(val movie: Movie) : MovieDetailsUiState
        data class Error(val message: String) : MovieDetailsUiState
        data class Idle : MovieDetailsUiState
    }
}