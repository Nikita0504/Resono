package com.dev.resono.player

import com.dev.domain.model.AudioMetadataPatch
import com.dev.domain.model.MediaFile

enum class PlayerSheetMode {
    Hidden,
    Mini,
    Expanded,
}

data class AppPlayerUiState(
    val sheetMode: PlayerSheetMode = PlayerSheetMode.Hidden,
    val currentAudioId: String? = null,
    val optimisticAudio: MediaFile.Audio? = null,
    val waveformSamples: List<Float> = emptyList(),
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
    data class ToggleFavorite(val trackId: String, val favorite: Boolean) : AppPlayerIntent
    data class EditMetadata(val trackId: String, val metadata: AudioMetadataPatch) : AppPlayerIntent
    data class HideCurrentTrack(val trackId: String) : AppPlayerIntent
}
