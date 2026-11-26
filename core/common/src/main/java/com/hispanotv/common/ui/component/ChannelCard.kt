package com.hispanotv.common.ui.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Transformation
import com.hispanotv.common.ui.theme.HispanoTVTheme
import com.hispanotv.common.ui.theme.PrimarySurfaceContainer
import com.hispanotv.domain.model.Channel

@Composable
fun ChannelCard(
    channel: Channel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    showFavorite: Boolean = true,
    onFavoriteClick: (() -> Unit)? = null
) {
    val isFocused = false // Will be handled by TV focus system
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            focusedElevation = 16.dp
        ),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isFocused) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Channel Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (channel.logoUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(channel.logoUrl)
                            .crossfade(true)
                            .transformations(Transformation { it.circleCrop() })
                            .build(),
                        contentDescription = channel.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(id = com.hispanotv.common.R.drawable.placeholder_channel),
                        error = painterResource(id = com.hispanotv.common.R.drawable.placeholder_channel)
                    )
                } else {
                    // Fallback placeholder
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawRect(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            size = size
                        )
                    }
                }
            }

            // Channel Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = channel.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (showFavorite && onFavoriteClick != null) {
                        androidx.compose.material3.IconButton(onClick = onFavoriteClick) {
                            androidx.compose.material3.Icon(
                                imageVector = if (channel.isFavorite)
                                    androidx.compose.material.icons.filled.Favorite
                                else
                                    androidx.compose.material.icons.outlined.Favorite,
                                contentDescription = if (channel.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                                tint = if (channel.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Country flag + Category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getCountryFlag(channel.countryCode) + " ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = channel.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelCardLarge(
    channel: Channel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .width(320.dp)
            .height(240.dp)
            .clickable(onClick = onClick),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            focusedElevation = 24.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (channel.logoUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(channel.logoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = channel.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(id = com.hispanotv.common.R.drawable.placeholder_channel),
                        error = painterResource(id = com.hispanotv.common.R.drawable.placeholder_channel)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, bottom = 12.dp)
            ) {
                Text(
                    text = channel.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getCountryFlag(channel.countryCode),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = channel.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    if (channel.isLive) {
                        LiveBadge()
                    }
                }

                if (onFavoriteClick != null) {
                    androidx.compose.material3.IconButton(onClick = onFavoriteClick) {
                        androidx.compose.material3.Icon(
                            imageVector = if (channel.isFavorite)
                                androidx.compose.material.icons.filled.Favorite
                            else
                                androidx.compose.material.icons.outlined.Favorite,
                            contentDescription = if (channel.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (channel.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveBadge() {
    Box(
        modifier = Modifier
            .padding(4.dp, 8.dp)
            .background(
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "EN VIVO",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onError
        )
    }
}

private fun getCountryFlag(countryCode: String): String {
    return when (countryCode.uppercase()) {
        "AR" -> "🇦🇷"
        "BO" -> "🇧🇴"
        "BR" -> "🇧🇷"
        "CL" -> "🇨🇱"
        "CO" -> "🇨🇴"
        "CR" -> "🇨🇷"
        "CU" -> "🇨🇺"
        "DO" -> "🇩🇴"
        "EC" -> "🇪🇨"
        "SV" -> "🇸🇻"
        "ES" -> "🇪🇸"
        "GT" -> "🇬🇹"
        "HN" -> "🇭🇳"
        "MX" -> "🇲🇽"
        "NI" -> "🇳🇮"
        "PA" -> "🇵🇦"
        "PY" -> "🇵🇾"
        "PE" -> "🇵🇪"
        "PR" -> "🇵🇷"
        "UY" -> "🇺🇾"
        "VE" -> "🇻🇪"
        "US" -> "🇺🇸"
        else -> "🏳️"
    }
}