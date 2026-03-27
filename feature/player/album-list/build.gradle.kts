plugins {
    id("android.dev.feature")
    id("android.dev.test")
}

android {
    namespace = "com.dev.albumlist"
}

dependencies {
    implementation(projects.core.ui.component)
    implementation(projects.core.ui.theme)
    implementation(projects.domain)
}
