package com.hispanotv.data.api

import com.hispanotv.domain.model.Genre
import com.hispanotv.domain.model.Movie
import com.hispanotv.domain.model.MovieDetailsResponse
import com.hispanotv.domain.model.MovieListResponse
import com.hispanotv.domain.model.MovieVideosResponse
import com.hispanotv.domain.model.WatchProvidersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1,
        @Query("region") region: String = "ES"
    ): MovieListResponse

    @GET("movie/popular")
    suspend fun getPopular(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1,
        @Query("region") region: String = "ES"
    ): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRated(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1,
        @Query("region") region: String = "ES"
    ): MovieListResponse

    @GET("movie/upcoming")
    suspend fun getUpcoming(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1,
        @Query("region") region: String = "ES"
    ): MovieListResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("region") region: String = "ES"
    ): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("append_to_response") appendToResponse: String = "videos,watch/providers,credits,similar,recommendations"
    ): MovieDetailsResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): MovieVideosResponse

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getWatchProviders(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): WatchProvidersResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): GenreResponse
}

data class GenreResponse(
    val genres: List<Genre>
)