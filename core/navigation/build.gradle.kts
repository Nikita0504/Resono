plugins {
    id("android.dev.feature")
    id("android.dev.test")
}

android {
    namespace = "com.dev.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}