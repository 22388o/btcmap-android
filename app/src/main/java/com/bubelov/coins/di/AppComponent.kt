package com.bubelov.coins.di

import android.content.Context
import com.bubelov.coins.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DatabaseModule::class,
        AndroidInjectionModule::class,
        ActivityBuilder::class,
        FragmentBuilder::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    fun inject(app: App)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}