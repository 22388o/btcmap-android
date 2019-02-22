buildscript {
    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
        classpath("org.jetbrains.kotlin:kotlin-android-extensions:1.3.21")
        classpath("com.google.gms:google-services:4.2.0")
        classpath("android.arch.navigation:navigation-safe-args-gradle-plugin:1.0.0-beta02")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}