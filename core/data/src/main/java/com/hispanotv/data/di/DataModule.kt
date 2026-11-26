package com.hispanotv.data.di

import android.content.Context
import com.hispanotv.data.local.AppDatabase
import com.hispanotv.data.local.dao.ChannelDao
import com.hispanotv.data.local.dao.CountryDao
import com.hispanotv.data.local.dao.MovieDao
import com.hispanotv.data.parser.M3uParser
import com.hispanotv.data.source.M3uChannelRepository
import com.hispanotv.data.source.M3uCountryRepository
import com.hispanotv.data.source.M3uDataSource
import com.hispanotv.data.source.TmdbRepository
import com.hispanotv.domain.repository.ChannelRepository
import com.hispanotv.domain.repository.CountryRepository
import com.hispanotv.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ApplicationScoped

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideChannelDao(database: AppDatabase): ChannelDao = database.channelDao()

    @Provides
    fun provideCountryDao(database: AppDatabase): CountryDao = database.countryDao()

    @Provides
    fun provideMovieDao(database: AppDatabase): MovieDao = database.movieDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideTmdbApi(retrofit: Retrofit): com.hispanotv.data.api.TmdbApi {
        return retrofit.create(com.hispanotv.data.api.TmdbApi::class.java)
    }

    @Provides
    @Singleton
    fun provideM3uUrls(): List<String> {
        return listOf(
            // Fuentes IPTV legales y gratuitas (ejemplos - reemplazar con URLs reales)
            "https://iptv-org.github.io/iptv/countries/es.m3u",
            "https://iptv-org.github.io/iptv/countries/mx.m3u",
            "https://iptv-org.github.io/iptv/countries/ar.m3u",
            "https://iptv-org.github.io/iptv/countries/co.m3u",
            "https://iptv-org.github.io/iptv/countries/cl.m3u",
            "https://iptv-org.github.io/iptv/countries/pe.m3u",
            "https://iptv-org.github.io/iptv/countries/ve.m3u",
            "https://iptv-org.github.io/iptv/countries/uy.m3u",
            "https://iptv-org.github.io/iptv/countries/bo.m3u",
            "https://iptv-org.github.io/iptv/countries/py.m3u",
            "https://iptv-org.github.io/iptv/countries/ec.m3u",
            "https://iptv-org.github.io/iptv/countries/do.m3u",
            "https://iptv-org.github.io/iptv/countries/gt.m3u",
            "https://iptv-org.github.io/iptv/countries/hn.m3u",
            "https://iptv-org.github.io/iptv/countries/ni.m3u",
            "https://iptv-org.github.io/iptv/countries/cr.m3u",
            "https://iptv-org.github.io/iptv/countries/pa.m3u",
            "https://iptv-org.github.io/iptv/countries/sv.m3u",
            "https://iptv-org.github.io/iptv/countries/cu.m3u",
            "https://iptv-org.github.io/iptv/countries/pr.m3u",
            "https://iptv-org.github.io/iptv/countries/us.m3u"
        )
    }

    @Provides
    @Singleton
    fun provideM3uParser(): M3uParser = M3uParser

    @Provides
    @Singleton
    fun provideM3uDataSource(
        okHttpClient: OkHttpClient,
        m3uUrls: List<String>
    ): M3uDataSource {
        return M3uDataSource(okHttpClient, m3uUrls)
    }

    @Provides
    @Singleton
    fun provideChannelRepository(dataSource: M3uDataSource): ChannelRepository {
        return M3uChannelRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideCountryRepository(dataSource: M3uDataSource): CountryRepository {
        return M3uCountryRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        api: com.hispanotv.data.api.TmdbApi,
        movieDao: MovieDao,
        @ApplicationScoped apiKey: String
    ): MovieRepository {
        return TmdbRepository(api, movieDao, apiKey)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @ApplicationScoped
    fun provideTmdbApiKey(): String {
        // TODO: Obtener de BuildConfig o secure storage
        return "TU_TMDB_API_KEY_AQUI"
    }
}