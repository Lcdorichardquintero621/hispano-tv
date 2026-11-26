package com.hispanotv.tv.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hispanotv.domain.model.Channel
import com.hispanotv.domain.repository.ChannelRepository
import com.hispanotv.domain.usecase.channel.GetChannelsByCountryUseCase
import com.hispanotv.domain.usecase.channel.SearchChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val getChannelsByCountryUseCase: GetChannelsByCountryUseCase,
    private val searchChannelsUseCase: SearchChannelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Loading)
    val uiState: StateFlow<ChannelUiState> = _uiState

    private var currentCountryCode = ""
    private var currentQuery = ""

    fun loadChannels(countryCode: String) {
        currentCountryCode = countryCode
        currentQuery = ""
        viewModelScope.launch {
            _uiState.value = ChannelUiState.Loading
            getChannelsByCountryUseCase(countryCode).collect { result ->
                _uiState.value = when (result) {
                    is ChannelRepository.Result.Success -> ChannelUiState.Success(result.data, countryCode, "")
                    is ChannelRepository.Result.Error -> ChannelUiState.Error(result.userMessage)
                }
            }
        }
    }

    fun searchChannels(query: String) {
        currentQuery = query
        if (query.isBlank()) {
            if (currentCountryCode.isNotEmpty()) {
                loadChannels(currentCountryCode)
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = ChannelUiState.Loading
            searchChannelsUseCase(query).collect { result ->
                _uiState.value = when (result) {
                    is ChannelRepository.Result.Success -> ChannelUiState.Success(result.data, currentCountryCode, query)
                    is ChannelRepository.Result.Error -> ChannelUiState.Error(result.userMessage)
                }
            }
        }
    }

    fun refreshChannels() {
        if (currentCountryCode.isNotEmpty()) {
            loadChannels(currentCountryCode)
        }
    }

    sealed interface ChannelUiState {
        data class Loading : ChannelUiState
        data class Success(
            val channels: List<Channel>,
            val countryCode: String,
            val query: String
        ) : ChannelUiState
        data class Error(val message: String) : ChannelUiState
    }
}