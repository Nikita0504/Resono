package com.dev.tracklist

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object TrackListGraph

@Serializable
internal object TrackListHomeRoute

fun NavGraphBuilder.trackListNavGraph() {
    navigation<TrackListGraph>(startDestination = TrackListHomeRoute) {
        composable<TrackListHomeRoute> {
            TrackListRoute {
                TrackListScreen()
            }
        }
    }
}

@Composable
private fun TrackListRoute(content: @Composable () -> Unit) {
    content()
}
