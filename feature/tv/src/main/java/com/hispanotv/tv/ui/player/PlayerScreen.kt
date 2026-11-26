package com.hispanotv.tv.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.hispanotv.domain.model.Channel
import com.hispanotv.domain.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val channelRepository: ChannelRepository
) : ViewModel() {

    private val channelId: String = savedStateHandle.get<String>("channelId") ?: ""

    private val _channelState = MutableStateFlow<Channel?>(null)
    val channelState: StateFlow<Channel?> = _channelState.asStateFlow()

    init {
        viewModelScope.launch {
            _channelState.value = channelRepository.getChannelById(channelId)
        }
    }
}

@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val channel by viewModel.channelState.collectAsStateWithLifecycle()
    val current = channel

    Box(modifier = Modifier.fillMaxSize()) {
        if (current == null) {
            LoadingPlayer()
        } else {
            PlayerContent(channel = current)
        }
    }
}

@Composable
fun PlayerContent(channel: Channel) {
    val context = LocalContext.current
    val player = remember(channel.streamUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(channel.streamUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AndroidPlayerView(player = player, modifier = Modifier.fillMaxSize())
        }
        ChannelInfoOverlay(channel = channel)
    }
}

@Composable
fun AndroidPlayerView(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
                controllerShowTimeoutMs = 3000
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
        },
        update = { view -> view.player = player }
    )
}

@Composable
fun ChannelInfoOverlay(channel: Channel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (channel.logoUrl != null) {
            AsyncImage(
                model = channel.logoUrl,
                contentDescription = channel.name,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier.padding(start = 12.dp).weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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

@Composable
fun LiveIndicator() {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "EN VIVO",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onError
        )
    }
}

@Composable
fun LoadingPlayer() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
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