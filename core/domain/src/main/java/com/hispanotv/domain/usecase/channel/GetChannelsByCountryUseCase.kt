package com.hispanotv.domain.usecase.channel

import com.hispanotv.domain.model.Channel
import com.hispanotv.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow

class GetChannelsByCountryUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {
    operator fun invoke(countryCode: String): Flow<ChannelRepository.Result<List<Channel>>> =
        channelRepository.getChannelsByCountry(countryCode).asFlow()
            .catch { e ->
                emit(ChannelRepository.Result.Error(e, "Error al cargar canales"))
            }
}