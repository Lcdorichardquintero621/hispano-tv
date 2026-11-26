package com.hispanotv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hispanotv.domain.model.Movie
import com.hispanotv.data.local.converter.Converters
import kotlinx.serialization.Serializable

@Entity(tableName = "movies")
@Serializable
@TypeConverters(Converters::class)
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val originalTitle: String?,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String,
    val genresJson: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val adult: Boolean,
    val originalLanguage: String,
    val runtime: Int?,
    val status: String,
    val tagline: String?,
    val budget: Long?,
    val revenue: Long?,
    val productionCompaniesJson: String,
    val productionCountriesJson: String,
    val spokenLanguagesJson: String,
    val videosJson: String,
    val watchProvidersJson: String?,
    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): Movie {
        return Movie(
            id = id,
            title = title,
            originalTitle = originalTitle,
            overview = overview,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            releaseDate = releaseDate,
            genres = Converters.fromGenresJson(genresJson),
            voteAverage = voteAverage,
            voteCount = voteCount,
            popularity = popularity,
            adult = adult,
            originalLanguage = originalLanguage,
            runtime = runtime,
            status = Movie.Status.valueOf(status),
            tagline = tagline,
            budget = budget,
            revenue = revenue,
            productionCompanies = Converters.fromProductionCompaniesJson(productionCompaniesJson),
            productionCountries = Converters.fromProductionCountriesJson(productionCountriesJson),
            spokenLanguages = Converters.fromSpokenLanguagesJson(spokenLanguagesJson),
            videos = Converters.fromMovieVideosJson(videosJson),
            watchProviders = watchProvidersJson?.let { Converters.fromWatchProvidersJson(it) },
            isFavorite = isFavorite,
            isInWatchlist = isInWatchlist
        )
    }

    companion object {
        fun fromDomain(movie: Movie): MovieEntity {
            return MovieEntity(
                id = movie.id,
                title = movie.title,
                originalTitle = movie.originalTitle,
                overview = movie.overview,
                posterUrl = movie.posterUrl,
                backdropUrl = movie.backdropUrl,
                releaseDate = movie.releaseDate,
                genresJson = Converters.toGenresJson(movie.genres),
                voteAverage = movie.voteAverage,
                voteCount = movie.voteCount,
                popularity = movie.popularity,
                adult = movie.adult,
                originalLanguage = movie.originalLanguage,
                runtime = movie.runtime,
                status = movie.status.name,
                tagline = movie.tagline,
                budget = movie.budget,
                revenue = movie.revenue,
                productionCompaniesJson = Converters.toProductionCompaniesJson(movie.productionCompanies),
                productionCountriesJson = Converters.toProductionCountriesJson(movie.productionCountries),
                spokenLanguagesJson = Converters.toSpokenLanguagesJson(movie.spokenLanguages),
                videosJson = Converters.toMovieVideosJson(movie.videos),
                watchProvidersJson = movie.watchProviders?.let { Converters.toWatchProvidersJson(it) },
                isFavorite = movie.isFavorite,
                isInWatchlist = movie.isInWatchlist
            )
        }
    }
}