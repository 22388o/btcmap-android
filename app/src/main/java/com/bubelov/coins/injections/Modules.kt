package com.bubelov.coins.injections

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import db.Database
import com.bubelov.coins.api.ConnectivityCheckingInterceptor
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.editplace.EditPlaceViewModel
import com.bubelov.coins.map.MapViewModel
import com.bubelov.coins.model.Location
import com.bubelov.coins.notificationarea.NotificationAreaViewModel
import com.bubelov.coins.notifications.PlaceNotificationManager
import com.bubelov.coins.picklocation.PickLocationResultViewModel
import com.bubelov.coins.profile.ProfileViewModel
import com.bubelov.coins.rates.ExchangeRatesViewModel
import com.bubelov.coins.repository.LocationRepository
import com.bubelov.coins.repository.PreferencesRepository
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.place.BuiltInPlacesCacheImpl
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.repository.rate.Bitstamp
import com.bubelov.coins.repository.rate.Coinbase
import com.bubelov.coins.repository.rate.ExchangeRatesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.search.PlacesSearchResultViewModel
import com.bubelov.coins.search.PlacesSearchViewModel
import com.bubelov.coins.settings.SettingsViewModel
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.sync.DatabaseSyncScheduler
import com.bubelov.coins.util.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

val module = module {

    viewModelOf(::EditPlaceViewModel)
    viewModelOf(::ExchangeRatesViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::NotificationAreaViewModel)
    viewModelOf(::PlacesSearchViewModel)
    viewModelOf(::PlacesSearchResultViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::PickLocationResultViewModel)
    viewModelOf(::ProfileViewModel)

    singleOf(::PlacesRepository)
    singleOf(::ExchangeRatesRepository)
    singleOf(::UserRepository)
    singleOf(::LocationRepository)
    singleOf(::NotificationAreaRepository)
    singleOf(::PlaceIconsRepository)
    singleOf(::LogsRepository)
    singleOf(::PreferencesRepository)

    single { Database(get()) }
    single { get<Database>().placeQueries }
    single { get<Database>().preferenceQueries }
    single { get<Database>().logEntryQueries }

    singleOf(::PlaceNotificationManager)

    singleOf(::DatabaseSync)
    singleOf(::DatabaseSyncScheduler)

    single<BuiltInPlacesCache> {
        BuiltInPlacesCacheImpl(get(), get())
    }

    singleOf(::Bitstamp)
    singleOf(::Coinbase)

    single { Location(40.7141667, -74.0063889) } // TODO remove

    single {
        GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, DateTimeAdapter())
            .create()
    }

    single { get<Context>().resources }
    single { get<Context>().assets }
    single { get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = get(),
            name = "data.db"
        )
    }

    single<CoinsApi> {
        val context: Context = get()
        val gson: Gson = get()
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(
                ConnectivityCheckingInterceptor(
                    connectivityManager
                )
            )
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://api.coin-map.com/v1/")
            .client(client)
            .build()

        retrofit.create(CoinsApi::class.java)
    }
}