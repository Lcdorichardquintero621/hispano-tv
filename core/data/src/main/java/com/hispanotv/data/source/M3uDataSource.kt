package com.hispanotv.data.source

import com.hispanotv.data.parser.M3uParser
import com.hispanotv.domain.model.Channel
import com.hispanotv.domain.model.Country
import com.hispanotv.domain.repository.ChannelRepository
import com.hispanotv.domain.repository.CountryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class M3uDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val m3uUrls: List<String>
) {
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val channelsFlow = _channels.asStateFlow()
    val countriesFlow = _countries.asStateFlow()
    val isLoadingFlow = _isLoading.asStateFlow()
    val errorFlow = _error.asStateFlow()

    suspend fun loadChannels() {
        _isLoading.value = true
        _error.value = null

        try {
            val allChannels = mutableListOf<Channel>()
            val allCountriesMap = mutableMapOf<String, Int>()

            for (url in m3uUrls) {
                val result = withContext(Dispatchers.IO) {
                    fetchAndParseM3u(url)
                }
                allChannels.addAll(result.channels)
                result.countries.forEach { (code, count) ->
                    allCountriesMap[code] = allCountriesMap.getOrDefault(code, 0) + count
                }
            }

            val domainChannels = M3uParser.toDomainChannels(allChannels)
            val domainCountries = M3uParser.toDomainCountries(
                M3uParser.ParsedM3u(allChannels, allCountriesMap)
            )

            _channels.value = domainChannels
            _countries.value = domainCountries
        } catch (e: Exception) {
            _error.value = "Error al cargar canales: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchAndParseM3u(url: String): M3uParser.ParsedM3u {
        val request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("HTTP ${response.code}: ${response.message}")
        }

        val body = response.body?.string()
            ?: throw IOException("Empty response")

        return M3uParser.parse(body)
    }
}

class M3uChannelRepository @Inject constructor(
    private val dataSource: M3uDataSource
) : ChannelRepository {
    override suspend fun getCountries(): Result<List<Country>> {
        return try {
            dataSource.loadChannels()
            Result.Success(dataSource.countriesFlow.value)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener países")
        }
    }

    override suspend fun getChannelsByCountry(countryCode: String): Result<List<Channel>> {
        return try {
            Result.Success(
                dataSource.channelsFlow.value.filter { it.countryCode == countryCode }
            )
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener canales")
        }
    }

    override suspend fun getChannelsByCategory(category: Channel.ChannelCategory): Result<List<Channel>> {
        return try {
            Result.Success(
                dataSource.channelsFlow.value.filter { it.category == category }
            )
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener canales por categoría")
        }
    }

    override suspend fun searchChannels(query: String): Result<List<Channel>> {
        return try {
            Result.Success(
                dataSource.channelsFlow.value.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            )
        } catch (e: Exception) {
            Result.Error(e, "Error en la búsqueda")
        }
    }

    override suspend fun getFavoriteChannels(): Result<List<Channel>> {
        return try {
            Result.Success(
                dataSource.channelsFlow.value.filter { it.isFavorite }
            )
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener favoritos")
        }
    }

    override suspend fun toggleFavorite(channelId: String): Result<Boolean> {
        return try {
            val channel = dataSource.channelsFlow.value.find { it.id == channelId }
                ?: throw IllegalArgumentException("Canal no encontrado")
            val newFavorite = !channel.isFavorite
            // TODO: Persist to local DB
            Result.Success(newFavorite)
        } catch (e: Exception) {
            Result.Error(e, "Error al actualizar favorito")
        }
    }

    override suspend fun refreshChannels(): Result<Unit> {
        return try {
            dataSource.loadChannels()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Error al actualizar")
        }
    }

    override val countriesFlow = dataSource.countriesFlow
    override val favoriteChannelsFlow = dataSource.channelsFlow.map {
        it.filter { it.isFavorite }
    }
}

class M3uCountryRepository @Inject constructor(
    private val dataSource: M3uDataSource
) : CountryRepository {
    override suspend fun getAllCountries(): Result<List<Country>> {
        return try {
            Result.Success(dataSource.countriesFlow.value)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener países")
        }
    }

    override suspend fun getPopularCountries(): Result<List<Country>> {
        return try {
            Result.Success(
                dataSource.countriesFlow.value.filter { it.isPopular }
            )
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener países populares")
        }
    }

    override suspend fun getCountryByCode(code: String): Result<Country?> {
        return try {
            Result.Success(
                dataSource.countriesFlow.value.find { it.code == code }
            )
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener país")
        }
    }

    override suspend fun updateChannelCount(code: String, count: Int): Result<Unit> {
        return try {
            // Update happens when channels are loaded
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Error al actualizar contador")
        }
    }

    override val countriesFlow = dataSource.countriesFlow
}