plugins {
    id("android.dev.library")
    id("tech.dev.koin")
}

android {
    namespace = "com.dev.data.repository"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.data.local)
    implementation(projects.data.database)
    implementation(projects.data.network)
    implementation(projects.core.logger)

    implementation(libs.koin.android)
}
