plugins {
    id("android.dev.library")
    id("tech.dev.koin")
}

android {
    namespace = "com.dev.player"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:logger"))

    implementation(libs.bundles.media3.player)
    implementation(libs.media3.datasource.okhttp)
}
