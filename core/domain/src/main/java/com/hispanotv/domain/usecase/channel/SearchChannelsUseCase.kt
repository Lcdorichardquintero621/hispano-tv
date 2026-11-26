package com.hispanotv.domain.usecase.channel

import com.hispanotv.domain.model.Channel
import com.hispanotv.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow

class SearchChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {
    operator fun invoke(query: String): Flow<ChannelRepository.Result<List<Channel>>> =
        if (query.isBlank()) {
            flowOf(ChannelRepository.Result.Success(emptyList()))
        } else {
            channelRepository.searchChannels(query).asFlow()
                .catch { e ->
                    emit(ChannelRepository.Result.Error(e, "Error en la búsqueda"))
                }
        }
}