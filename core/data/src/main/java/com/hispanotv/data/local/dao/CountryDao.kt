package com.hispanotv.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hispanotv.data.local.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryEntity>)

    @Query("SELECT * FROM countries ORDER BY isPopular DESC, name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE isPopular = 1 ORDER BY name ASC")
    fun getPopularCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE code = :code")
    suspend fun getCountryByCode(code: String): CountryEntity?

    @Query("UPDATE countries SET channelCount = :count WHERE code = :code")
    suspend fun updateChannelCount(code: String, count: Int)
}