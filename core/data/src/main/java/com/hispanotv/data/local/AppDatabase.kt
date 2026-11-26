package com.hispanotv.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hispanotv.data.local.converter.Converters
import com.hispanotv.data.local.dao.ChannelDao
import com.hispanotv.data.local.dao.CountryDao
import com.hispanotv.data.local.dao.MovieDao
import com.hispanotv.data.local.entity.ChannelEntity
import com.hispanotv.data.local.entity.CountryEntity
import com.hispanotv.data.local.entity.MovieEntity

@Database(
    entities = [ChannelEntity::class, CountryEntity::class, MovieEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun countryDao(): CountryDao
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hispanotv_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}