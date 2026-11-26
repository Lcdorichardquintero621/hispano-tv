package com.hispanotv.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val streamUrl: String,
    val countryCode: String,
    val category: ChannelCategory,
    val isFavorite: Boolean = false,
    val isLive: Boolean = true,
    val epgId: String? = null,
    val drmScheme: String? = null,
    val drmLicenseUrl: String? = null,
) {
    @Serializable
    enum class ChannelCategory(val displayName: String, val icon: String) {
        GENERAL("General", "📺"),
        NEWS("Noticias", "📰"),
        SPORTS("Deportes", "⚽"),
        ENTERTAINMENT("Entretenimiento", "🎭"),
        MOVIES("Películas", "🎬"),
        KIDS("Infantil", "🧸"),
        MUSIC("Música", "🎵"),
        DOCUMENTARY("Documentales", "📚"),
        RELIGIOUS("Religioso", "⛪"),
        SHOPPING("Compras", "🛍️"),
        OTHER("Otros", "📡")
    }
}

@Serializable
data class Country(
    val code: String,
    val name: String,
    val flagEmoji: String,
    val channelCount: Int = 0,
    val isPopular: Boolean = false
)

@Serializable
data class ChannelGroup(
    val category: Channel.ChannelCategory,
    val channels: List<Channel>
)