package com.hispanotv.tv.ui.player

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.hispanotv.common.ui.theme.HispanoTVTheme
import com.hispanotv.domain.model.Channel
import com.hispanotv.domain.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Composable
fun PlayerScreen(
    navController: androidx.navigation.NavController,
    viewModel: PlayerViewModel = viewModel()
) {
    val channelId = navController.getString("channelId") ?: ""
    val channel by viewModel.channelState.collectAsStateWithLifecycle()

    LaunchedEffect(channelId) {
        if (channelId.isNotEmpty()) {
            viewModel.loadChannel(channelId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            channel == null -> LoadingPlayer()
            else -> PlayerContent(channel = channel)
        }
    }
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : androidx.lifecycle.ViewModel() {

    private val _channelState = MutableStateFlow<Channel?>(null)
    val channelState: StateFlow<Channel?> = _channelState

    fun loadChannel(channelId: String) {
        // For now, we'll get the channel from the repository
        // In a real app, you'd have a dedicated use case for this
        androidx.lifecycle.viewModelScope.launch {
            val result = channelRepository.getChannelsByCountry("") // This is a workaround
            // Better: add getChannelById to repository
        }
    }
}

@Composable
fun PlayerContent(channel: Channel) {
    val context = LocalContext.current
    val player = remember { createPlayer(context, channel.streamUrl) }
    val controllerVisibility by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Player View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .weight(1f)
        ) {
            AndroidPlayerView(player = player, modifier = Modifier.fillMaxSize())
        }

        // Channel Info Overlay
        if (controllerVisibility) {
            ChannelInfoOverlay(channel = channel)
        }
    }
}

@Composable
fun AndroidPlayerView(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    androidx.compose.ui.viewinterop.AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                setPlayer(player)
                useController = true
                controllerShowTimeoutMs = 3000
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
        },
        update = { view ->
            view.player = player
        }
    )
}

@Composable
fun ChannelInfoOverlay(channel: Channel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (channel.logoUrl != null) {
                    coil.compose.AsyncImage(
                        model = channel.logoUrl!!,
                        contentDescription = channel.name,
                        modifier = Modifier.size(48.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 12.dp).weight(1f)
                ) {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${getCountryFlag(channel.countryCode)} ${channel.category.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }

                if (channel.isLive) {
                    LiveIndicator()
                }
            }
        }
    }
}

@Composable
fun LiveIndicator() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.error,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            androidx.compose.animation.graphics.vector.AnimatedVisibility(
                visible = true,
                enter = androidx.compose.animation.core.expandHorizontally(),
                exit = androidx.compose.animation.core.shrinkHorizontally()
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(
                        color = MaterialTheme.colorScheme.onError,
                        radius = 4.dp.toPx()
                    )
                }
            }
            Text(
                text = "EN VIVO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}

@Composable
fun LoadingPlayer() {
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

private fun createPlayer(context: android.content.Context, streamUrl: String): ExoPlayer {
    val player = ExoPlayer.Builder(context).build()

    // Determine stream type and create appropriate media source
    val mediaItem = when {
        streamUrl.endsWith(".m3u8") -> {
            MediaItem.fromUri(streamUrl).copy(
                mediaId = streamUrl,
                mediaSource = com.google.android.exoplayer2.source.hls.HlsMediaSource.Factory(
                    com.google.android.exoplayer2.upstream.DefaultHttpDataSource.Factory()
                ).createMediaSource(
                    com.google.android.exoplayer2.MediaItem.fromUri(streamUrl)
                )
            )
        }
        streamUrl.startsWith("rtmp") || streamUrl.startsWith("rtsp") -> {
            MediaItem.fromUri(streamUrl)
        }
        else -> {
            MediaItem.fromUri(streamUrl)
        }
    }

    player.setMediaItem(mediaItem)
    player.prepare()
    player.playWhenReady = true

    return player
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