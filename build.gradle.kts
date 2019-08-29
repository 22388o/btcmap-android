buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
        classpath("org.jetbrains.kotlin:kotlin-android-extensions:1.3.41")
        classpath("android.arch.navigation:navigation-safe-args-gradle-plugin:1.0.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}