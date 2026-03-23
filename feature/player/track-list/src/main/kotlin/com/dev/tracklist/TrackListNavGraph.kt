package com.dev.tracklist

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object TrackListGraph

@Serializable
internal object TrackListHomeRoute

fun NavGraphBuilder.trackListNavGraph() {
    navigation<TrackListGraph>(startDestination = TrackListHomeRoute) {
        composable<TrackListHomeRoute> {
            TrackListRoute()
        }
    }
}

@Composable
private fun TrackListRoute() {
    val viewModel = koinViewModel<AudioListViewModel>()
    val photos = viewModel.photos.collectAsStateWithLifecycle()
    TrackListScreen(photos = photos.value)
}
