package com.dev.tracklist

import com.dev.domain.model.MediaFile

data class TrackListUiState(
    val tracks: List<MediaFile.Audio> = emptyList(),
)

sealed interface TrackListIntent {
    data class TrackClicked(val index: Int) : TrackListIntent
}

sealed interface TrackListEffect {
    data class PlayTracks(
        val tracks: List<MediaFile.Audio>,
        val startIndex: Int,
    ) : TrackListEffect
}
