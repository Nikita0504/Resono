pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Resono"


include(":app")
include(":domain")
include(":data")
include(":data:database")
include(":data:network")
include(":data:repository")
include(":core:logger")
include(":core:ui")
include(":player:core")
include(":feature:player:album-list")
include(":feature:gallery:media-library")
include(":feature:player:track-list")
include(":feature:player:player")
include(":core:navigation")
