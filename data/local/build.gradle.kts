plugins {
    id("android.dev.library")
    id("tech.dev.koin")
}

android {
    namespace = "com.dev.local"
}

dependencies {
    implementation(projects.domain)
}
