package com.hispanotv.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hispanotv.domain.model.Genre
import com.hispanotv.domain.model.MovieVideo
import com.hispanotv.domain.model.ProductionCompany
import com.hispanotv.domain.model.ProductionCountry
import com.hispanotv.domain.model.Provider
import com.hispanotv.domain.model.SpokenLanguage
import com.hispanotv.domain.model.WatchProviders
import com.hispanotv.domain.model.CountryWatchProviders
import com.hispanotv.domain.model.VideoType
import com.hispanotv.domain.model.MovieStatus

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGenresJson(json: String): List<Genre> {
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toGenresJson(list: List<Genre>): String = gson.toJson(list)

    @TypeConverter
    fun fromProductionCompaniesJson(json: String): List<ProductionCompany> {
        val type = object : TypeToken<List<ProductionCompany>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toProductionCompaniesJson(list: List<ProductionCompany>): String = gson.toJson(list)

    @TypeConverter
    fun fromProductionCountriesJson(json: String): List<ProductionCountry> {
        val type = object : TypeToken<List<ProductionCountry>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toProductionCountriesJson(list: List<ProductionCountry>): String = gson.toJson(list)

    @TypeConverter
    fun fromSpokenLanguagesJson(json: String): List<SpokenLanguage> {
        val type = object : TypeToken<List<SpokenLanguage>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toSpokenLanguagesJson(list: List<SpokenLanguage>): String = gson.toJson(list)

    @TypeConverter
    fun fromMovieVideosJson(json: String): List<MovieVideo> {
        val type = object : TypeToken<List<MovieVideo>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toMovieVideosJson(list: List<MovieVideo>): String = gson.toJson(list)

    @TypeConverter
    fun fromWatchProvidersJson(json: String): WatchProviders {
        val type = object : TypeToken<WatchProviders>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toWatchProvidersJson(wp: WatchProviders): String = gson.toJson(wp)
}

@TypeConverter
fun videoTypeToString(type: VideoType): String = type.name

@TypeConverter
fun stringToVideoType(name: String): VideoType = VideoType.valueOf(name)

@TypeConverter
fun movieStatusToString(status: MovieStatus): String = status.name

@TypeConverter
fun stringToMovieStatus(name: String): MovieStatus = MovieStatus.valueOf(name)