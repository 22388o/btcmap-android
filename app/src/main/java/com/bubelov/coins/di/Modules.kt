package com.bubelov.coins.di

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import com.bubelov.coins.Database
import com.bubelov.coins.api.ConnectivityCheckingInterceptor
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.MockCoinsApi
import com.bubelov.coins.auth.AuthResultViewModel
import com.bubelov.coins.auth.AuthViewModel
import com.bubelov.coins.editplace.EditPlaceViewModel
import com.bubelov.coins.launcher.LauncherViewModel
import com.bubelov.coins.logs.LogsViewModel
import com.bubelov.coins.map.MapViewModel
import com.bubelov.coins.model.Location
import com.bubelov.coins.notificationarea.NotificationAreaViewModel
import com.bubelov.coins.notifications.PlaceNotificationManager
import com.bubelov.coins.permissions.PermissionsViewModel
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
import org.koin.android.experimental.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

val mainModule = module {
    viewModel<EditPlaceViewModel>()
    viewModel<ExchangeRatesViewModel>()
    viewModel<MapViewModel>()
    viewModel<NotificationAreaViewModel>()
    viewModel<PlacesSearchViewModel>()
    viewModel<PlacesSearchResultViewModel>()
    viewModel<SettingsViewModel>()
    viewModel<AuthViewModel>()
    viewModel<AuthResultViewModel>()
    viewModel<PickLocationResultViewModel>()
    viewModel<LauncherViewModel>()
    viewModel<PermissionsViewModel>()
    viewModel<ProfileViewModel>()
    viewModel<LogsViewModel>()

    single<PlacesRepository>()
    single<ExchangeRatesRepository>()
    single<UserRepository>()
    single<LocationRepository>()
    single<NotificationAreaRepository>()
    single<PlaceIconsRepository>()
    single<LogsRepository>()
    single<PreferencesRepository>()

    single { Database(get()) }
    single { get<Database>().placeQueries }
    single { get<Database>().preferenceQueries }
    single { get<Database>().logEntryQueries }

    single<PlaceNotificationManager>()

    single<DatabaseSync>()
    single<DatabaseSyncScheduler>()

    single<BuiltInPlacesCache> {
        BuiltInPlacesCacheImpl(get(), get(), get())
    }

    single<Bitstamp>()
    single<Coinbase>()

    single { Location(40.7141667, -74.0063889) } // TODO remove

    single {
        GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, DateTimeAdapter())
            .create()
    }
}

val androidModule = module {
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
}

val apiModule = module {
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
            .addConverterFactory(JsonStringConverterFactory(GsonConverterFactory.create()))
            .baseUrl("https://api.coin-map.com/v1/")
            .client(client)
            .build()

        retrofit.create(CoinsApi::class.java)
    }
}

val mockApiModule = module {
    single<CoinsApi> {
        MockCoinsApi(get(), get())
    }
}