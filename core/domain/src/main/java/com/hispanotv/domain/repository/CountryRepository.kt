package com.hispanotv.domain.repository

import com.hispanotv.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    suspend fun getAllCountries(): Result<List<Country>>
    suspend fun getPopularCountries(): Result<List<Country>>
    suspend fun getCountryByCode(code: String): Result<Country?>
    suspend fun updateChannelCount(code: String, count: Int): Result<Unit>

    val countriesFlow: Flow<List<Country>>

    sealed interface Result<out T> {
        data class Success<out T>(val data: T) : Result<T>
        data class Error(val throwable: Throwable, val userMessage: String) : Result<Nothing>
    }
}