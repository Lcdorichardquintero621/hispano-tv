package com.hispanotv.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hispanotv.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Update
    suspend fun updateAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY lastUpdated DESC")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isInWatchlist = 1 ORDER BY lastUpdated DESC")
    fun getWatchlistMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' ORDER BY popularity DESC")
    fun searchMovies(query: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: String): MovieEntity?

    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE movies SET isInWatchlist = :isInWatchlist WHERE id = :id")
    suspend fun updateWatchlist(id: String, isInWatchlist: Boolean)

    @Query("DELETE FROM movies WHERE lastUpdated < :threshold")
    suspend fun deleteOldMovies(threshold: Long)
}