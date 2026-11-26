package com.hispanotv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hispanotv.domain.model.Channel
import kotlinx.serialization.Serializable

@Entity(tableName = "channels")
@Serializable
data class ChannelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logoUrl: String?,
    val streamUrl: String,
    val countryCode: String,
    val category: String,
    val isFavorite: Boolean = false,
    val isLive: Boolean = true,
    val epgId: String? = null,
    val drmScheme: String? = null,
    val drmLicenseUrl: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): Channel {
        return Channel(
            id = id,
            name = name,
            logoUrl = logoUrl,
            streamUrl = streamUrl,
            countryCode = countryCode,
            category = Channel.ChannelCategory.valueOf(category),
            isFavorite = isFavorite,
            isLive = isLive,
            epgId = epgId,
            drmScheme = drmScheme,
            drmLicenseUrl = drmLicenseUrl
        )
    }

    companion object {
        fun fromDomain(channel: Channel): ChannelEntity {
            return ChannelEntity(
                id = channel.id,
                name = channel.name,
                logoUrl = channel.logoUrl,
                streamUrl = channel.streamUrl,
                countryCode = channel.countryCode,
                category = channel.category.name,
                isFavorite = channel.isFavorite,
                isLive = channel.isLive,
                epgId = channel.epgId,
                drmScheme = channel.drmScheme,
                drmLicenseUrl = channel.drmLicenseUrl
            )
        }
    }
}