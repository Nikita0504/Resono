plugins {
    id("android.dev.application")
    id("android.dev.compose")
    id("android.dev.test")
    id("tech.dev.koin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.dev.resono"
}

dependencies {
    implementation(projects.core.logger)
    implementation(projects.core.navigation)
    implementation(projects.domain)
    implementation(projects.data.local)
    implementation(projects.data.repository)
    implementation(projects.player.core)
    implementation(projects.feature.player.player)
    implementation(projects.feature.player.albumList)
    implementation(projects.feature.player.trackList)
    implementation(projects.feature.gallery.mediaLibrary)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}
