package com.dev.tracklist

import com.dev.domain.model.MediaFile
import com.dev.domain.model.AudioMetadataPatch

data class TrackListUiState(
    val tracks: List<MediaFile.Audio> = emptyList(),
    val hiddenTracks: List<MediaFile.Audio> = emptyList(),
    val showHiddenTracks: Boolean = false,
)

sealed interface TrackListIntent {
    data class TrackClicked(val index: Int) : TrackListIntent
    data class ToggleShowHidden(val showHidden: Boolean) : TrackListIntent
    data class ToggleFavorite(val trackId: String, val favorite: Boolean) : TrackListIntent
    data class HideTrack(val trackId: String) : TrackListIntent
    data class RestoreTrack(val trackId: String) : TrackListIntent
    data class EditMetadata(val trackId: String, val metadata: AudioMetadataPatch) : TrackListIntent
}

sealed interface TrackListEffect {
    data class PlayTracks(
        val tracks: List<MediaFile.Audio>,
        val startIndex: Int,
    ) : TrackListEffect
}
