package com.dev.medialibrary

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object MediaLibraryGraph

@Serializable
internal object MediaLibraryHomeRoute

fun NavGraphBuilder.mediaLibraryNavGraph() {
    navigation<MediaLibraryGraph>(startDestination = MediaLibraryHomeRoute) {
        composable<MediaLibraryHomeRoute> {
            MediaLibraryRoute {
                MediaLibraryScreen()
            }
        }
    }
}

@Composable
private fun MediaLibraryRoute(content: @Composable () -> Unit) {
    content()
}
