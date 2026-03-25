plugins {
    id("android.dev.feature")
    id("android.dev.test")
}

android {
    namespace = "com.dev.feature.player"
}

dependencies {
    implementation(projects.domain)
    implementation(libs.androidx.compose.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
