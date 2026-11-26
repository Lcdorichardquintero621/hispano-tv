package com.hispanotv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hispanotv.domain.model.Country
import kotlinx.serialization.Serializable

@Entity(tableName = "countries")
@Serializable
data class CountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val flagEmoji: String,
    val channelCount: Int = 0,
    val isPopular: Boolean = false
) {
    fun toDomain(): Country {
        return Country(
            code = code,
            name = name,
            flagEmoji = flagEmoji,
            channelCount = channelCount,
            isPopular = isPopular
        )
    }

    companion object {
        fun fromDomain(country: Country): CountryEntity {
            return CountryEntity(
                code = country.code,
                name = country.name,
                flagEmoji = country.flagEmoji,
                channelCount = country.channelCount,
                isPopular = country.isPopular
            )
        }
    }
}