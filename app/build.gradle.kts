plugins {
    id("android.dev.application")
    id("android.dev.compose")
    id("android.dev.test")
    id("tech.dev.koin")
}

android {
    namespace = "com.dev.resono"
}

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.feature.player.player)
    implementation(projects.feature.player.albumList)
    implementation(projects.feature.player.trackList)
    implementation(projects.feature.gallery.mediaLibrary)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}