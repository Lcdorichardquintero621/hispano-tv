package com.hispanotv.tv.ui.channel

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavController
import androidx.navigation.compose.getString
import com.hispanotv.common.ui.component.ChannelCard
import com.hispanotv.common.ui.theme.HispanoTVTheme
import com.hispanotv.domain.model.Channel
import com.hispanotv.tv.ui.player.PlayerScreen

@Composable
fun ChannelGridScreen(
    navController: NavController,
    viewModel: ChannelViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val countryCode = navController.getString("countryCode") ?: ""
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load channels when countryCode changes
    androidx.compose.runtime.LaunchedEffect(countryCode) {
        if (countryCode.isNotEmpty()) {
            viewModel.loadChannels(countryCode)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Search Bar
            SearchBar(
                query = uiState is ChannelViewModel.ChannelUiState.Success ? uiState.query : "",
                onQueryChange = { query -> viewModel.searchChannels(query) },
                onClear = { viewModel.searchChannels("") }
            )

            // Content
            when (uiState) {
                is ChannelViewModel.ChannelUiState.Loading -> {
                    LoadingScreen()
                }
                is ChannelViewModel.ChannelUiState.Error -> {
                    ErrorScreen(message = uiState.message, onRetry = { viewModel.refreshChannels() })
                }
                is ChannelViewModel.ChannelUiState.Success -> {
                    ChannelGridContent(
                        channels = uiState.channels,
                        countryCode = uiState.countryCode,
                        query = uiState.query,
                        onChannelClick = { channel ->
                            navController.navigate("player/${channel.id}")
                        },
                        onFavoriteClick = { channelId ->
                            // TODO: Implement favorite toggle
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val textFieldValue = androidx.compose.runtime.mutableStateOf(query)

    androidx.compose.material3.TextField(
        value = textFieldValue.value,
        onValueChange = { newValue ->
            textFieldValue.value = newValue
            onQueryChange(newValue)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        label = { Text(text = "Buscar canales...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Limpiar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    androidx.compose.foundation.clickable(onClick = {
                        textFieldValue.value = ""
                        onClear()
                    })
                }
            }
        },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        ),
        keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
            onSearch = { onQueryChange(textFieldValue.value) }
        ),
        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun ChannelGridContent(
    channels: List<Channel>,
    countryCode: String,
    query: String,
    onChannelClick: (Channel) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (query.isNotEmpty()) {
                        "Resultados para \"$query\" (${channels.size})"
                    } else {
                        "Canales de ${getCountryName(countryCode)} (${channels.size})"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (channels.isEmpty()) {
            EmptyState(
                message = if (query.isNotEmpty()) {
                    "No se encontraron canales para \"$query\""
                } else {
                    "No hay canales disponibles para este país"
                },
                icon = if (query.isNotEmpty()) {
                    Icons.Filled.SearchOff
                } else {
                    Icons.Filled.TvOff
                }
            )
        } else {
            // Group by category
            val groupedChannels = channels
                .groupBy { it.category }
                .toList()
                .sortedBy { it.key.ordinal }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(groupedChannels) { (category, categoryChannels) ->
                    // Category header
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Channels in category
                    items(categoryChannels) { channel ->
                        ChannelCard(
                            channel = channel,
                            modifier = Modifier
                                .width(220.dp)
                                .height(200.dp),
                            onClick = { onChannelClick(channel) },
                            onFavoriteClick = { onFavoriteClick(channel.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
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
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
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

private fun getCountryName(countryCode: String): String {
    return when (countryCode.uppercase()) {
        "AR" -> "Argentina"
        "BO" -> "Bolivia"
        "BR" -> "Brasil"
        "CL" -> "Chile"
        "CO" -> "Colombia"
        "CR" -> "Costa Rica"
        "CU" -> "Cuba"
        "DO" -> "República Dominicana"
        "EC" -> "Ecuador"
        "SV" -> "El Salvador"
        "ES" -> "España"
        "GT" -> "Guatemala"
        "HN" -> "Honduras"
        "MX" -> "México"
        "NI" -> "Nicaragua"
        "PA" -> "Panamá"
        "PY" -> "Paraguay"
        "PE" -> "Perú"
        "PR" -> "Puerto Rico"
        "UY" -> "Uruguay"
        "VE" -> "Venezuela"
        "US" -> "Estados Unidos"
        else -> countryCode
    }
}