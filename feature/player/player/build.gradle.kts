plugins {
    id("android.dev.feature")
    id("android.dev.test")
}

android {
    namespace = "com.dev.feature.player"
}

dependencies {
    implementation(projects.core.ui.component)
    implementation(projects.core.ui.theme)
    implementation(projects.domain)
    implementation(libs.androidx.compose.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
