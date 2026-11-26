package com.hispanotv.tv.ui.country

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hispanotv.common.ui.theme.HispanoTVTheme
import com.hispanotv.common.ui.theme.PrimarySurfaceContainer
import com.hispanotv.domain.model.Country
import com.hispanotv.tv.ui.channel.ChannelGridScreen
import androidx.navigation.compose.NavController
import androidx.navigation.compose.navArgs

@Composable
fun CountryListScreen(
    navController: NavController,
    viewModel: CountryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is CountryViewModel.CountryUiState.Loading -> {
                LoadingScreen()
            }
            is CountryViewModel.CountryUiState.Error -> {
                ErrorScreen(message = uiState.message, onRetry = { viewModel.loadCountries() })
            }
            is CountryViewModel.CountryUiState.Success -> {
                CountryListContent(
                    countries = uiState.countries,
                    onCountryClick = { country ->
                        navController.navigate("channels/${country.code}")
                    }
                )
            }
        }
    }
}

@Composable
fun CountryListContent(
    countries: List<Country>,
    onCountryClick: (Country) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "TV en Vivo por País",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Popular countries row
        val popularCountries = countries.filter { it.isPopular }
        if (popularCountries.isNotEmpty()) {
            CountryRow(
                title = "Países Populares",
                countries = popularCountries,
                onCountryClick = onCountryClick,
                isPopular = true
            )
        }

        // All countries
        CountryRow(
            title = "Todos los Países",
            countries = countries,
            onCountryClick = onCountryClick,
            isPopular = false
        )
    }
}

@Composable
fun CountryRow(
    title: String,
    countries: List<Country>,
    onCountryClick: (Country) -> Unit,
    isPopular: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp)
        ) {
            items(countries) { country ->
                CountryCard(
                    country = country,
                    isPopular = isPopular,
                    onClick = { onCountryClick(country) }
                )
            }
        }
    }
}

@Composable
fun CountryCard(
    country: Country,
    isPopular: Boolean,
    onClick: () -> Unit
) {
    val cardWidth = if (isPopular) 200.dp else 160.dp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(200.dp)
            .clickable(onClick = onClick),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            focusedElevation = 16.dp
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flag
            Text(
                text = country.flagEmoji,
                fontSize = 64.sp,
                modifier = Modifier.padding(top = 24.dp)
            )

            // Country name
            Text(
                text = country.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Channel count
            Text(
                text = "${country.channelCount} canales",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Popular badge
            if (isPopular) {
                Box(
                    modifier = Modifier
                        .padding(4.dp, 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "POPULAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.filled.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            androidx.compose.material3.FilledButton(onClick = onRetry) {
                Text(text = "Reintentar")
            }
        }
    }
}