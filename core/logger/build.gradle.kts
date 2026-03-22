plugins {
    id("android.dev.library")
    id("tech.dev.koin")
}

android {
    namespace = "com.dev.logger"

    buildFeatures {
        buildConfig = true
    }
}