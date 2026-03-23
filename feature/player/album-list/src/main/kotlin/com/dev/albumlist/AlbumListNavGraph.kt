package com.dev.albumlist

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object AlbumListGraph

@Serializable
internal object AlbumListHomeRoute

fun NavGraphBuilder.albumListNavGraph() {
    navigation<AlbumListGraph>(startDestination = AlbumListHomeRoute) {
        composable<AlbumListHomeRoute> {
            AlbumListRoute {
                AlbumListScreen()
            }
        }
    }
}

@Composable
private fun AlbumListRoute(content: @Composable () -> Unit) {
    content()
}
