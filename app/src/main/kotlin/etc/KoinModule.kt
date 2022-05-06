package etc

import android.content.Context
import android.location.LocationManager
import com.google.gson.GsonBuilder
import db.Database
import map.MapViewModel
import notificationarea.NotificationAreaViewModel
import settings.PreferencesRepository
import notificationarea.NotificationAreaRepository
import map.BuiltInPlacesCache
import map.BuiltInPlacesCacheImpl
import map.PlacesRepository
import map.PlaceIconsRepository
import search.PlacesSearchResultViewModel
import search.PlacesSearchViewModel
import settings.SettingsViewModel
import sync.DatabaseSync
import sync.DatabaseSyncScheduler
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.time.LocalDateTime

val koinModule = module {

    viewModelOf(::MapViewModel)
    viewModelOf(::PlacesSearchViewModel)
    viewModelOf(::PlacesSearchResultViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::NotificationAreaViewModel)

    singleOf(::PlacesRepository)
    singleOf(::PlaceIconsRepository)
    singleOf(::LocationRepository)
    singleOf(::PreferencesRepository)
    singleOf(::NotificationAreaRepository)

    single { Database(get()) }
    single { get<Database>().placeQueries }
    single { get<Database>().preferenceQueries }

    singleOf(::PlaceNotificationManager)

    singleOf(::DatabaseSync)
    singleOf(::DatabaseSyncScheduler)

    single<BuiltInPlacesCache> {
        BuiltInPlacesCacheImpl(get(), get())
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

    single {
        GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, DateTimeAdapter())
            .create()
    }
}