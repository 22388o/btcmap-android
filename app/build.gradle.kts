import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "com.bubelov.coins"

        minSdkVersion(21)
        targetSdkVersion(28)

        versionCode = 41
        versionName = "2.0.5-RC1"

        project.ext.set("archivesBaseName", "Coins-" + defaultConfig.versionName)

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/src/main/assets/schemas")
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_URL", "\"https://api.coin-map.com/v1/\"")
        buildConfigField("Boolean", "MOCK_API", "false")

        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"557789430086-hnt5bk65a636mgi7phhn5t9b1porppik.apps.googleusercontent.com\"")

        buildConfigField("Float", "MAP_MARKER_ANCHOR_U", "0.5f")
        buildConfigField("Float", "MAP_MARKER_ANCHOR_V", "0.91145f")

        buildConfigField("Double", "DEFAULT_LOCATION_LAT", "40.7141667")
        buildConfigField("Double", "DEFAULT_LOCATION_LON", "-74.0063889")

        manifestPlaceholders = mapOf("crashlyticsEnabled" to true)
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders = mapOf("crashlyticsEnabled" to false)
        }

        getByName("release") {
            manifestPlaceholders = mapOf("crashlyticsEnabled" to true)
        }
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1")
    implementation("androidx.core:core-ktx:1.0.1")

    // Dependency injection
    implementation("com.google.dagger:dagger:2.21")
    kapt("com.google.dagger:dagger-android-processor:2.21")
    kapt("com.google.dagger:dagger-compiler:2.21")
    implementation("com.google.dagger:dagger-android-support:2.21")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.5.1-SNAPSHOT")
    implementation("com.squareup.retrofit2:converter-gson:2.5.1-SNAPSHOT")
    implementation("com.squareup.retrofit2:retrofit-mock:2.5.1-SNAPSHOT")
    implementation("com.squareup.okhttp3:logging-interceptor:3.10.0")

    // Cache
    implementation("androidx.room:room-runtime:2.1.0-alpha04")
    kapt("androidx.room:room-compiler:2.1.0-alpha04")

    // Navigation
    implementation("android.arch.navigation:navigation-ui-ktx:1.0.0-rc02")
    implementation("android.arch.navigation:navigation-fragment-ktx:1.0.0-rc02")

    // Scheduling
    implementation("android.arch.work:work-runtime-ktx:1.0.0")
    androidTestImplementation("android.arch.work:work-testing:1.0.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")

    // Compatibility
    implementation("androidx.appcompat:appcompat:1.1.0-alpha02")
    implementation("com.google.android.material:material:1.1.0-alpha04")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-alpha3")
    implementation("androidx.browser:browser:1.0.0")

    // Google services
    implementation("com.google.android.gms:play-services-auth:16.0.1")
    implementation("com.google.android.gms:play-services-maps:16.1.0")
    implementation("com.google.firebase:firebase-core:16.0.7")
    implementation("com.google.maps.android:android-maps-utils:0.5")

    // JSON
    implementation("com.google.code.gson:gson:2.8.5")

    // Images
    implementation("com.squareup.picasso:picasso:2.5.2")

    // Logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Dates handling
    implementation("joda-time:joda-time:2.10.1")

    // Crash reporting
    implementation("com.crashlytics.sdk.android:crashlytics:2.8.0@aar") {
        isTransitive = true
    }

    // Unit tests
    testImplementation("junit:junit:4.12")
    testImplementation("androidx.arch.core:core-testing:2.0.0")

    // Mocks
    testImplementation("org.mockito:mockito-core:2.23.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0-RC1")

    // Instrumented tests
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.1.1")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test:rules:1.1.1")
    androidTestImplementation("androidx.room:room-testing:2.1.0-alpha04")
}

androidExtensions {
    configure(delegateClosureOf<AndroidExtensionsExtension> {
        isExperimental = true
    })
}

apply(plugin = "com.google.gms.google-services")