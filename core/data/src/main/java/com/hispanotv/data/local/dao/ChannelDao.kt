package com.hispanotv.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hispanotv.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<ChannelEntity>)

    @Update
    suspend fun updateAll(channels: List<ChannelEntity>)

    @Query("SELECT * FROM channels WHERE countryCode = :countryCode ORDER BY name ASC")
    fun getChannelsByCountry(countryCode: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchChannels(query: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE category = :category ORDER BY name ASC")
    fun getChannelsByCategory(category: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE id = :id")
    suspend fun getChannelById(id: String): ChannelEntity?

    @Query("UPDATE channels SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM channels")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM channels WHERE countryCode = :countryCode")
    suspend fun countByCountry(countryCode: String): Int
}