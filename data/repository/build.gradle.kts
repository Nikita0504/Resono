plugins {
    id("android.dev.library")
    id("tech.dev.koin")
}

android {
    namespace = "com.dev.data.repository"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data:database"))
    implementation(project(":data:network"))
    implementation(project(":core:logger"))

    implementation(libs.koin.android)
}
