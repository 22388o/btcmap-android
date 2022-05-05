package injections

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import db.Database
import api.ConnectivityCheckingInterceptor
import api.coins.CoinsApi
import editplace.EditPlaceViewModel
import map.MapViewModel
import model.Location
import notificationarea.NotificationAreaViewModel
import notifications.PlaceNotificationManager
import picklocation.PickLocationResultViewModel
import profile.ProfileViewModel
import rates.ExchangeRatesViewModel
import repository.LocationRepository
import repository.PreferencesRepository
import repository.area.NotificationAreaRepository
import repository.place.BuiltInPlacesCache
import repository.place.BuiltInPlacesCacheImpl
import repository.place.PlacesRepository
import repository.placeicon.PlaceIconsRepository
import repository.rate.Bitstamp
import repository.rate.Coinbase
import repository.rate.ExchangeRatesRepository
import repository.synclogs.LogsRepository
import repository.user.UserRepository
import search.PlacesSearchResultViewModel
import search.PlacesSearchViewModel
import settings.SettingsViewModel
import sync.DatabaseSync
import sync.DatabaseSyncScheduler
import etc.*
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