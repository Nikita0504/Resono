plugins {
    id("android.dev.feature")
    id("android.dev.test")
}

android {
    namespace = "com.dev.theme"
}

dependencies {
    implementation(projects.core.ui.model)
}
