package com.dev.resono.player

import com.dev.domain.model.MediaFile

enum class PlayerSheetMode {
    Hidden,
    Mini,
    Expanded,
}

data class AppPlayerUiState(
    val sheetMode: PlayerSheetMode = PlayerSheetMode.Hidden,
)

sealed interface AppPlayerIntent {
    data class PlayFromTrackList(
        val tracks: List<MediaFile.Audio>,
        val startIndex: Int,
    ) : AppPlayerIntent

    data object Expand : AppPlayerIntent
    data object Collapse : AppPlayerIntent
    data object TogglePlayPause : AppPlayerIntent
    data object SkipToNext : AppPlayerIntent
    data object SkipToPrevious : AppPlayerIntent
    data class SeekTo(val positionMs: Long) : AppPlayerIntent
}
