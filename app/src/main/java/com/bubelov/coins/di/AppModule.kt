package com.bubelov.coins.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import com.bubelov.coins.BuildConfig
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.MockCoinsApi
import com.bubelov.coins.db.Database
import com.bubelov.coins.model.Location
import com.bubelov.coins.repository.currency.BuiltInCurrenciesCache
import com.bubelov.coins.repository.currencyplace.BuiltInCurrenciesPlacesCache
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.placecategory.BuiltInPlaceCategoriesCache
import com.bubelov.coins.util.DateTimeAdapter
import com.bubelov.coins.util.JsonStringConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
class AppModule {
    @Provides
    @Named("default_location")
    fun provideLocation() = Location(40.7141667, -74.0063889)

    @Provides
    fun providePlacesDb(database: Database) = database.placesDb()

    @Provides
    fun providePlaceCategoriesDb(database: Database) = database.placeCategoriesDb()

    @Provides
    fun provideCurrenciesPlacesDb(database: Database) = database.currenciesPlacesDb()

    @Provides
    fun providePreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeAdapter())
            .create()
    }

    @Provides
    fun provideCoroutineContext(): CoroutineContext {
        return Dispatchers.Main
    }

    @Provides
    @Singleton
    fun provideApi(
        gson: Gson,
        currenciesCache: BuiltInCurrenciesCache,
        placesCache: BuiltInPlacesCache,
        currenciesPlacesCache: BuiltInCurrenciesPlacesCache,
        placeCategoriesCache: BuiltInPlaceCategoriesCache
    ): CoinsApi {
        return if (!BuildConfig.MOCK_API) {
            createApi(gson)
        } else {
            createMockApi(
                currenciesCache,
                placesCache,
                currenciesPlacesCache,
                placeCategoriesCache
            )
        }
    }

    private fun createApi(gson: Gson): CoinsApi {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(JsonStringConverterFactory(GsonConverterFactory.create()))
            .client(client)
            .build()
            .create(CoinsApi::class.java)
    }

    private fun createMockApi(
        currenciesCache: BuiltInCurrenciesCache,
        placesCache: BuiltInPlacesCache,
        currenciesPlacesCache: BuiltInCurrenciesPlacesCache,
        placeCategoriesCache: BuiltInPlaceCategoriesCache
    ): CoinsApi {
        return MockCoinsApi(
            currenciesCache = currenciesCache,
            placesCache = placesCache,
            currenciesPlacesCache = currenciesPlacesCache,
            placeCategoriesCache = placeCategoriesCache
        )
    }
}