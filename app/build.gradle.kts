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
    implementation(libs.androidx.lifecycle.runtime.ktx)
}