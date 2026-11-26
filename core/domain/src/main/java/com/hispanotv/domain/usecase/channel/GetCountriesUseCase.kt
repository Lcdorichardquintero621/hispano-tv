package com.hispanotv.domain.usecase.channel

import com.hispanotv.domain.model.Country
import com.hispanotv.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow

class GetCountriesUseCase @Inject constructor(
    private val countryRepository: CountryRepository
) {
    operator fun invoke(): Flow<CountryRepository.Result<List<Country>>> = countryRepository.countriesFlow
        .map { countries ->
            CountryRepository.Result.Success(countries.sortedByDescending { it.isPopular }.thenBy { it.name })
        }
        .catch { e ->
            emit(CountryRepository.Result.Error(e, "Error al cargar países"))
        }
}