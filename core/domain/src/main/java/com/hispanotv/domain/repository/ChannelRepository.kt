package com.hispanotv.domain.repository

import com.hispanotv.domain.model.Channel
import com.hispanotv.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    suspend fun getCountries(): Result<List<Country>>
    suspend fun getChannelsByCountry(countryCode: String): Result<List<Channel>>
    suspend fun getChannelsByCategory(category: Channel.ChannelCategory): Result<List<Channel>>
    suspend fun searchChannels(query: String): Result<List<Channel>>
    suspend fun getFavoriteChannels(): Result<List<Channel>>
    suspend fun toggleFavorite(channelId: String): Result<Boolean>
    suspend fun refreshChannels(): Result<Unit>
    suspend fun getChannelById(channelId: String): Channel?

    // Flow for reactive UI
    val countriesFlow: Flow<List<Country>>
    val favoriteChannelsFlow: Flow<List<Channel>>

    sealed interface Result<out T> {
        data class Success<out T>(val data: T) : Result<T>
        data class Error(val throwable: Throwable, val userMessage: String) : Result<Nothing>
    }
}