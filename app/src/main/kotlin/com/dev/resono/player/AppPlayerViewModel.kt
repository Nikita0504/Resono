package com.dev.resono.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.domain.model.MediaFile
import com.dev.domain.model.PlayableMedia
import com.dev.domain.model.PlayerState
import com.dev.domain.repository.PlayerRepository
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
        }
    }

    private fun playFromAudioList(tracks: List<MediaFile.Audio>, startIndex: Int) {
        if (tracks.isEmpty()) return

        val playlist = tracks.map { audio ->
            PlayableMedia.Audio(audio)
        }

        viewModelScope.launch {
            playerRepository.prepare(playlist, startIndex)
            playerRepository.play()
            updateSheetMode(PlayerSheetMode.Mini)
        }
    }

    private fun onPlayPauseClick() {
        viewModelScope.launch {
            if (playerState.value.isPlaying) {
                playerRepository.pause()
            } else {
                playerRepository.play()
            }
        }
    }

    private fun onNextClick() {
        viewModelScope.launch {
            playerRepository.skipToNext()
        }
    }

    private fun onPreviousClick() {
        viewModelScope.launch {
            playerRepository.skipToPrevious()
        }
    }

    private fun onSeekTo(positionMs: Long) {
        viewModelScope.launch {
            playerRepository.seekTo(positionMs)
        }
    }

    private fun updateSheetMode(mode: PlayerSheetMode) {
        _uiState.update { current ->
            if (current.sheetMode == mode) current else current.copy(sheetMode = mode)
        }
    }

    private fun reduceUiState(current: AppPlayerUiState, playerState: PlayerState): AppPlayerUiState {
        return when {
            playerState.currentItem == null -> current.copy(sheetMode = PlayerSheetMode.Hidden)
            current.sheetMode == PlayerSheetMode.Hidden -> current.copy(sheetMode = PlayerSheetMode.Mini)
            else -> current
        }
    }
}
