package com.hispanotv.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String,
    val genres: List<Genre>,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val adult: Boolean,
    val originalLanguage: String,
    val runtime: Int? = null,
    val status: MovieStatus = MovieStatus.RELEASED,
    val tagline: String? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
    val productionCompanies: List<ProductionCompany> = emptyList(),
    val productionCountries: List<ProductionCountry> = emptyList(),
    val spokenLanguages: List<SpokenLanguage> = emptyList(),
    val videos: List<MovieVideo> = emptyList(),
    val watchProviders: WatchProviders? = null,
    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,
) {
    val year: String
        get() = releaseDate.take(4)

    val formattedGenres: String
        get() = genres.take(3).joinToString(", ") { it.name }

    val ratingPercentage: Int
        get() = (voteAverage * 10).toInt()
}

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class ProductionCompany(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
)

@Serializable
data class ProductionCountry(
    val iso3166_1: String,
    val name: String
)

@Serializable
data class SpokenLanguage(
    val englishName: String,
    val iso639_1: String,
    val name: String
)

@Serializable
data class MovieVideo(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: VideoType,
    val official: Boolean,
    val publishedAt: String
) {
    val youtubeUrl: String
        get() = "https://www.youtube.com/watch?v=$key"
}

@Serializable
enum class VideoType {
    TRAILER, TEASER, CLIP, FEATURETTE, BEHIND_THE_SCENES, BLOOPER
}

@Serializable
data class WatchProviders(
    val results: Map<String, CountryWatchProviders>
)

@Serializable
data class CountryWatchProviders(
    val link: String?,
    val flatrate: List<Provider>?,
    val rent: List<Provider>?,
    val buy: List<Provider>?,
    val free: List<Provider>?
)

@Serializable
data class Provider(
    val providerId: Int,
    val providerName: String,
    val logoPath: String,
    val displayPriority: Int
)

@Serializable
enum class MovieStatus {
    RELEASED, RUMORED, PLANNED, IN_PRODUCTION, POST_PRODUCTION, CANCELED
}

@Serializable
data class MovieListResponse(
    val page: Int,
    val results: List<Movie>,
    val totalPages: Int,
    val totalResults: Int
)

@Serializable
data class MovieDetailsResponse(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val genres: List<Genre>,
    val voteAverage: Double,
    val voteCount: Int,
    val runtime: Int?,
    val status: String,
    val tagline: String?,
    val budget: Long?,
    val revenue: Long?,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<ProductionCountry>,
    val spokenLanguages: List<SpokenLanguage>,
    val videos: MovieVideosResponse?,
    val watchProviders: WatchProvidersResponse?
) {
    fun toMovie(imageBaseUrl: String = "https://image.tmdb.org/t/p/w500"): Movie {
        return Movie(
            id = id.toString(),
            title = title,
            originalTitle = null,
            overview = overview,
            posterUrl = posterPath?.let { "$imageBaseUrl$it" },
            backdropUrl = backdropPath?.let { "$imageBaseUrl$it" },
            releaseDate = releaseDate,
            genres = genres,
            voteAverage = voteAverage,
            voteCount = voteCount,
            popularity = 0.0,
            adult = false,
            originalLanguage = spokenLanguages.firstOrNull()?.iso639_1 ?: "es",
            runtime = runtime,
            status = runCatching {
                MovieStatus.valueOf(status.uppercase().replace(" ", "_"))
            }.getOrDefault(MovieStatus.RELEASED),
            tagline = tagline,
            budget = budget,
            revenue = revenue,
            productionCompanies = productionCompanies,
            productionCountries = productionCountries,
            spokenLanguages = spokenLanguages,
            videos = videos?.results?.map { it.toMovieVideo() } ?: emptyList(),
            watchProviders = watchProviders?.toWatchProviders()
        )
    }
}

@Serializable
data class MovieVideosResponse(
    val results: List<MovieVideoResponse>
)

@Serializable
data class MovieVideoResponse(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val official: Boolean,
    val publishedAt: String
) {
    fun toMovieVideo(): MovieVideo {
        return MovieVideo(
            id = id,
            key = key,
            name = name,
            site = site,
            type = runCatching {
                VideoType.valueOf(type.uppercase().replace(" ", "_"))
            }.getOrDefault(VideoType.CLIP),
            official = official,
            publishedAt = publishedAt
        )
    }
}

@Serializable
data class WatchProvidersResponse(
    val results: Map<String, CountryWatchProvidersResponse>
)

@Serializable
data class CountryWatchProvidersResponse(
    val link: String?,
    val flatrate: List<ProviderResponse>?,
    val rent: List<ProviderResponse>?,
    val buy: List<ProviderResponse>?,
    val free: List<ProviderResponse>?
) {
    fun toCountryWatchProviders(): CountryWatchProviders {
        return CountryWatchProviders(
            link = link,
            flatrate = flatrate?.map { it.toProvider() },
            rent = rent?.map { it.toProvider() },
            buy = buy?.map { it.toProvider() },
            free = free?.map { it.toProvider() }
        )
    }
}

@Serializable
data class ProviderResponse(
    val providerId: Int,
    val providerName: String,
    val logoPath: String,
    val displayPriority: Int
) {
    fun toProvider(): Provider {
        return Provider(
            providerId = providerId,
            providerName = providerName,
            logoPath = logoPath,
            displayPriority = displayPriority
        )
    }
}

fun WatchProvidersResponse.toWatchProviders(): WatchProviders {
    return WatchProviders(
        results = results.mapValues { (_, value) -> value.toCountryWatchProviders() }
    )
}
