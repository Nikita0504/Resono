package com.dev.resono.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.domain.model.MediaFile
import com.dev.domain.model.AudioMetadataPatch
import com.dev.domain.model.PlayableMedia
import com.dev.domain.model.PlayerState
import com.dev.domain.repository.PlayerRepository
import com.dev.domain.usecase.media.GetAudioPlaybackPositionUseCase
import com.dev.domain.usecase.media.SetAudioFavoriteUseCase
import com.dev.domain.usecase.media.SetAudioHiddenUseCase
import com.dev.domain.usecase.media.SetAudioMetadataUseCase
import com.dev.domain.usecase.media.SetAudioPlaybackPositionUseCase
import com.dev.domain.usecase.player.ObserveCurrentWaveformUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppPlayerViewModel(
    private val playerRepository: PlayerRepository,
    private val setAudioPlaybackPositionUseCase: SetAudioPlaybackPositionUseCase,
    private val getAudioPlaybackPositionUseCase: GetAudioPlaybackPositionUseCase,
    private val setAudioHiddenUseCase: SetAudioHiddenUseCase,
    private val setAudioFavoriteUseCase: SetAudioFavoriteUseCase,
    private val setAudioMetadataUseCase: SetAudioMetadataUseCase,
    private val observeCurrentWaveformUseCase: ObserveCurrentWaveformUseCase,
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = playerRepository.playerState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerState(),
        )

    private val _uiState = MutableStateFlow(AppPlayerUiState())
    val uiState: StateFlow<AppPlayerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playerState.collectLatest { state ->
                _uiState.update { current ->
                    reduceUiState(current, state)
                }
            }
        }
        viewModelScope.launch {
            observeCurrentWaveformUseCase().collectLatest { waveform ->
                _uiState.update { state ->
                    val currentId = state.currentAudioId
                    when {
                        waveform == null -> state.copy(waveformSamples = emptyList())
                        currentId == null -> state.copy(waveformSamples = emptyList())
                        waveform.mediaId != currentId -> state.copy(waveformSamples = emptyList())
                        else -> state.copy(waveformSamples = waveform.samples)
                    }
                }
            }
        }
    }

    fun onIntent(intent: AppPlayerIntent) {
        when (intent) {
            is AppPlayerIntent.PlayFromTrackList -> playFromAudioList(intent.tracks, intent.startIndex)
            AppPlayerIntent.Expand -> updateSheetMode(PlayerSheetMode.Expanded)
            AppPlayerIntent.Collapse -> updateSheetMode(PlayerSheetMode.Mini)
            AppPlayerIntent.TogglePlayPause -> onPlayPauseClick()
            AppPlayerIntent.SkipToNext -> onNextClick()
            AppPlayerIntent.SkipToPrevious -> onPreviousClick()
            is AppPlayerIntent.SeekTo -> onSeekTo(intent.positionMs)
            is AppPlayerIntent.ToggleFavorite -> onToggleFavorite(intent.trackId, intent.favorite)
            is AppPlayerIntent.EditMetadata -> onEditMetadata(intent.trackId, intent.metadata)
            is AppPlayerIntent.HideCurrentTrack -> onHideCurrentTrack(intent.trackId)
        }
    }

    private fun playFromAudioList(tracks: List<MediaFile.Audio>, startIndex: Int) {
        if (tracks.isEmpty()) return
        if (startIndex !in tracks.indices) return

        val playlist = tracks.map { audio ->
            PlayableMedia.Audio(audio)
        }
        val selectedTrackId = tracks[startIndex].id

        viewModelScope.launch {
            playerRepository.prepare(playlist, startIndex)
            getAudioPlaybackPositionUseCase(selectedTrackId)
                ?.takeIf { it > 0L }
                ?.let { savedPositionMs ->
                    playerRepository.seekTo(savedPositionMs)
                }
            playerRepository.play()
            updateSheetMode(PlayerSheetMode.Mini)
        }
    }

    private fun onPlayPauseClick() {
        viewModelScope.launch {
            if (playerState.value.isPlaying) {
                persistCurrentAudioPosition()
                playerRepository.pause()
            } else {
                playerRepository.play()
            }
        }
    }

    private fun onNextClick() {
        viewModelScope.launch {
            persistCurrentAudioPosition()
            playerRepository.skipToNext()
        }
    }

    private fun onPreviousClick() {
        viewModelScope.launch {
            persistCurrentAudioPosition()
            playerRepository.skipToPrevious()
        }
    }

    private fun onSeekTo(positionMs: Long) {
        viewModelScope.launch {
            playerRepository.seekTo(positionMs)
            currentAudioId()?.let { audioId ->
                setAudioPlaybackPositionUseCase(audioId, positionMs)
            }
        }
    }

    private fun updateSheetMode(mode: PlayerSheetMode) {
        _uiState.update { current ->
            if (current.sheetMode == mode) current else current.copy(sheetMode = mode)
        }
    }

    private fun reduceUiState(current: AppPlayerUiState, playerState: PlayerState): AppPlayerUiState {
        val currentAudioId = (playerState.currentItem as? PlayableMedia.Audio)?.file?.id
        val shouldResetOptimistic = current.currentAudioId != currentAudioId

        return when {
            playerState.currentItem == null -> current.copy(
                sheetMode = PlayerSheetMode.Hidden,
                currentAudioId = null,
                optimisticAudio = null,
                waveformSamples = emptyList(),
            )

            current.sheetMode == PlayerSheetMode.Hidden -> current.copy(
                sheetMode = PlayerSheetMode.Mini,
                currentAudioId = currentAudioId,
                optimisticAudio = if (shouldResetOptimistic) null else current.optimisticAudio,
            )

            shouldResetOptimistic -> current.copy(
                currentAudioId = currentAudioId,
                optimisticAudio = null,
                waveformSamples = emptyList(),
            )

            else -> current.copy(currentAudioId = currentAudioId)
        }
    }

    private suspend fun persistCurrentAudioPosition() {
        val audioId = currentAudioId() ?: return
        val positionMs = playerState.value.positionMs
        if (positionMs > 0L) {
            setAudioPlaybackPositionUseCase(audioId, positionMs)
        }
    }

    private fun currentAudioId(): String? {
        val current = playerState.value.currentItem as? PlayableMedia.Audio ?: return null
        return current.file.id
    }

    private fun onToggleFavorite(trackId: String, favorite: Boolean) {
        _uiState.update { state ->
            val updated = state.optimisticAudio?.takeIf { it.id == trackId }
                ?: currentAudioFile()?.takeIf { it.id == trackId }
                ?: return@update state

            state.copy(optimisticAudio = updated.copy(isFavorite = favorite))
        }
        viewModelScope.launch {
            setAudioFavoriteUseCase(trackId, favorite)
        }
    }

    private fun onEditMetadata(trackId: String, metadata: AudioMetadataPatch) {
        val normalized = metadata.normalized()
        _uiState.update { state ->
            if (normalized.isEmpty()) {
                return@update state.copy(optimisticAudio = null)
            }

            val updated = state.optimisticAudio?.takeIf { it.id == trackId }
                ?: currentAudioFile()?.takeIf { it.id == trackId }
                ?: return@update state

            state.copy(
                optimisticAudio = updated.copy(
                    title = normalized.title,
                    artist = normalized.artist.orEmpty(),
                    album = normalized.album.orEmpty(),
                    albumArtUri = normalized.albumArtUri,
                    trackNumber = normalized.trackNumber,
                    year = normalized.year,
                ),
            )
        }
        viewModelScope.launch {
            setAudioMetadataUseCase(trackId, normalized)
        }
    }

    private fun onHideCurrentTrack(trackId: String) {
        viewModelScope.launch {
            setAudioHiddenUseCase(trackId, true)
            _uiState.update { it.copy(optimisticAudio = null) }
            if (playerState.value.hasNext) {
                playerRepository.skipToNext()
            } else {
                playerRepository.clear()
            }
        }
    }

    private fun currentAudioFile(): MediaFile.Audio? {
        return (playerState.value.currentItem as? PlayableMedia.Audio)?.file
    }
}

private fun AudioMetadataPatch.normalized(): AudioMetadataPatch {
    return copy(
        title = title?.trim()?.takeIf { it.isNotEmpty() },
        artist = artist?.trim()?.takeIf { it.isNotEmpty() },
        album = album?.trim()?.takeIf { it.isNotEmpty() },
        albumArtUri = albumArtUri?.trim()?.takeIf { it.isNotEmpty() },
        trackNumber = trackNumber?.takeIf { it > 0 },
        year = year?.takeIf { it > 0 },
    )
}

private fun AudioMetadataPatch.isEmpty(): Boolean {
    return title == null &&
        artist == null &&
        album == null &&
        albumArtUri == null &&
        trackNumber == null &&
        year == null
}
