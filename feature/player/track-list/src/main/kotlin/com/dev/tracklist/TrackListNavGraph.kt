package com.dev.tracklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dev.domain.model.MediaFile
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object TrackListGraph

@Serializable
internal object TrackListHomeRoute

fun NavGraphBuilder.trackListNavGraph(
    onTrackSelected: (tracks: List<MediaFile.Audio>, startIndex: Int) -> Unit,
    onOpenAlbums: () -> Unit,
) {
    navigation<TrackListGraph>(startDestination = TrackListHomeRoute) {
        composable<TrackListHomeRoute> {
            TrackListRoute(
                onTrackSelected = onTrackSelected,
                onOpenAlbums = onOpenAlbums,
            )
        }
    }
}

@Composable
private fun TrackListRoute(
    onTrackSelected: (tracks: List<MediaFile.Audio>, startIndex: Int) -> Unit,
    onOpenAlbums: () -> Unit,
) {
    val viewModel = koinViewModel<AudioListViewModel>()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TrackListEffect.PlayTracks -> onTrackSelected(effect.tracks, effect.startIndex)
            }
        }
    }

    TrackListScreen(
        tracks = uiState.value.tracks,
        hiddenTracks = uiState.value.hiddenTracks,
        showHiddenTracks = uiState.value.showHiddenTracks,
        onOpenAlbums = onOpenAlbums,
        onTrackClick = { index ->
            viewModel.onIntent(TrackListIntent.TrackClicked(index))
        },
        onShowHiddenChanged = { showHidden ->
            viewModel.onIntent(TrackListIntent.ToggleShowHidden(showHidden))
        },
        onToggleFavorite = { trackId, favorite ->
            viewModel.onIntent(TrackListIntent.ToggleFavorite(trackId, favorite))
        },
        onHideTrack = { trackId ->
            viewModel.onIntent(TrackListIntent.HideTrack(trackId))
        },
        onRestoreTrack = { trackId ->
            viewModel.onIntent(TrackListIntent.RestoreTrack(trackId))
        },
        onEditMetadata = { trackId, metadata ->
            viewModel.onIntent(TrackListIntent.EditMetadata(trackId, metadata))
        },
    )
}
