package tech.dev

import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    "implementation"(libs.findBundle("ktor-client").get())
    "testImplementation"(libs.findLibrary("ktor-client-mock").get())
}