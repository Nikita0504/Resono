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
    implementation(project(":core:navigation"))
    implementation(project(":feature:player:player"))
    implementation(project(":feature:player:album-list"))
    implementation(project(":feature:player:track-list"))
    implementation(project(":feature:gallery:media-library"))
    implementation(libs.androidx.lifecycle.runtime.ktx)
}