package com.bubelov.coins.di

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.bubelov.coins.BuildConfig
import com.bubelov.coins.Database
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.MockCoinsApi
import com.bubelov.coins.auth.AuthResultViewModel
import com.bubelov.coins.auth.AuthViewModel
import com.bubelov.coins.editplace.EditPlaceViewModel
import com.bubelov.coins.launcher.LauncherViewModel
import com.bubelov.coins.map.MapViewModel
import com.bubelov.coins.model.Location
import com.bubelov.coins.notificationarea.NotificationAreaViewModel
import com.bubelov.coins.permissions.PermissionsViewModel
import com.bubelov.coins.picklocation.PickLocationResultViewModel
import com.bubelov.coins.placedetails.PlaceDetailsViewModel
import com.bubelov.coins.rates.ExchangeRatesViewModel
import com.bubelov.coins.repository.currency.BuiltInCurrenciesCache
import com.bubelov.coins.repository.currencyplace.BuiltInCurrenciesPlacesCache
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.placecategory.BuiltInPlaceCategoriesCache
import com.bubelov.coins.search.PlacesSearchResultViewModel
import com.bubelov.coins.search.PlacesSearchViewModel
import com.bubelov.coins.settings.SettingsViewModel
import com.bubelov.coins.util.ConnectivityCheckingInterceptor
import com.bubelov.coins.util.DateTimeAdapter
import com.bubelov.coins.util.JsonStringConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
val appModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(get()) }

    single { Database(get()) }

    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = get(),
            name = "data.db"
        )
    }

    single<CoroutineContext> { Dispatchers.Main }

    single {
        GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeAdapter())
            .create()
    }

    single { get<Context>().resources }

    single(named("default_location")) {
        Location(40.7141667, -74.0063889)
    }

    single { BuiltInCurrenciesCache(get(), get()) }
    single { BuiltInPlacesCache(get(), get()) }
    single { BuiltInCurrenciesPlacesCache(get(), get()) }
    single { BuiltInPlaceCategoriesCache(get(), get()) }

    viewModel { EditPlaceViewModel(get()) }
    viewModel { ExchangeRatesViewModel(get()) }
    viewModel { MapViewModel(get(), get(), get()) }
    viewModel { NotificationAreaViewModel(get(), get(), get(named("default_location"))) }
    viewModel { PlacesSearchViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { PlacesSearchResultViewModel() }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { AuthResultViewModel() }
    viewModel { PickLocationResultViewModel() }
    viewModel { PlaceDetailsViewModel(get(), get(), get()) }
    viewModel { LauncherViewModel(get()) }
    viewModel { PermissionsViewModel(get()) }
}

val apiModule = module {
    single<CoinsApi> {
        val context: Context = get()
        val gson: Gson = get()
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE }

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(ConnectivityCheckingInterceptor(connectivityManager))
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(JsonStringConverterFactory(GsonConverterFactory.create()))
            .baseUrl(BuildConfig.API_URL)
            .client(client)
            .build()

        retrofit.create(CoinsApi::class.java)
    }
}

val mockApiModule = module {
    single<CoinsApi> {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .build()

        val networkBehavior = NetworkBehavior.create()

        networkBehavior.setDelay(0, TimeUnit.MILLISECONDS)
        networkBehavior.setErrorPercent(0)

        val mockRetrofit = MockRetrofit.Builder(retrofit)
            .networkBehavior(networkBehavior)
            .build()

        val delegate = mockRetrofit.create(CoinsApi::class.java)

        MockCoinsApi(delegate, get(), get(), get(), get())
    }
}