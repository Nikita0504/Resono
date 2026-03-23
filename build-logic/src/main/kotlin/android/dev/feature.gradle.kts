@file:Suppress("UNUSED_VARIABLE")

package android.dev

import com.android.build.api.dsl.LibraryExtension
import helpers.configureKotlinAndroid
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

android {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

configureKotlinAndroid(extensions.getByType<LibraryExtension>())

dependencies {
    "implementation"(platform(libs.findLibrary("androidx-compose-bom").get()))
    //"implementation"(libs.findLibrary("androidx-compose-icons-extended").get())
    "implementation"(libs.findBundle("koin-android-full").get())
    "implementation"(libs.findBundle("compose-core").get())
}