package com.hispanotv.tv.ui.country

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hispanotv.domain.model.Country
import com.hispanotv.domain.repository.CountryRepository
import com.hispanotv.domain.usecase.channel.GetCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CountryUiState>(CountryUiState.Loading)
    val uiState: StateFlow<CountryUiState> = _uiState

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            getCountriesUseCase().collect { result ->
                _uiState.value = when (result) {
                    is CountryRepository.Result.Success -> CountryUiState.Success(result.data)
                    is CountryRepository.Result.Error -> CountryUiState.Error(result.userMessage)
                }
            }
        }
    }

    sealed interface CountryUiState {
        data class Loading : CountryUiState
        data class Success(val countries: List<Country>) : CountryUiState
        data class Error(val message: String) : CountryUiState
    }
}