plugins {
    id("android.dev.feature")
    id("android.dev.test")
}
android {
    namespace = "com.dev.component"
}

dependencies {
    implementation(projects.core.ui.theme)
    implementation(libs.coil.compose)
}
