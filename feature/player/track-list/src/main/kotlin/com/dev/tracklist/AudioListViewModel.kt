package com.dev.tracklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.domain.model.AudioMetadataPatch
import com.dev.domain.usecase.media.GetAudioFilesUseCase
import com.dev.domain.usecase.media.GetHiddenAudioFilesUseCase
import com.dev.domain.usecase.media.ScanLocalMediaUseCase
import com.dev.domain.usecase.media.SetAudioFavoriteUseCase
import com.dev.domain.usecase.media.SetAudioHiddenUseCase
import com.dev.domain.usecase.media.SetAudioMetadataUseCase
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AudioListViewModel(
    private val getAudioFilesUseCase: GetAudioFilesUseCase,
    private val getHiddenAudioFilesUseCase: GetHiddenAudioFilesUseCase,
    private val scanLocalMediaUseCase: ScanLocalMediaUseCase,
    private val setAudioHiddenUseCase: SetAudioHiddenUseCase,
    private val setAudioFavoriteUseCase: SetAudioFavoriteUseCase,
    private val setAudioMetadataUseCase: SetAudioMetadataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackListUiState())
    val uiState: StateFlow<TrackListUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<TrackListEffect>()
    val effects: SharedFlow<TrackListEffect> = _effects.asSharedFlow()

    init {
        observeTracks()
        observeHiddenTracks()
        viewModelScope.launch {
            scanLocalMediaUseCase()
        }
    }

    private fun observeTracks() {
        viewModelScope.launch {
            getAudioFilesUseCase().collectLatest { tracks ->
                _uiState.update { state -> state.copy(tracks = tracks) }
            }
        }
    }

    fun onIntent(intent: TrackListIntent) {
        when (intent) {
            is TrackListIntent.TrackClicked -> playTrack(intent.index)
            is TrackListIntent.ToggleShowHidden -> toggleShowHidden(intent.showHidden)
            is TrackListIntent.ToggleFavorite -> toggleFavorite(intent.trackId, intent.favorite)
            is TrackListIntent.HideTrack -> hideTrack(intent.trackId)
            is TrackListIntent.RestoreTrack -> restoreTrack(intent.trackId)
            is TrackListIntent.EditMetadata -> editMetadata(intent.trackId, intent.metadata)
        }
    }

    private fun playTrack(index: Int) {
        val tracks = uiState.value.tracks
        if (index !in tracks.indices) return

        viewModelScope.launch {
            _effects.emit(
                TrackListEffect.PlayTracks(
                    tracks = tracks,
                    startIndex = index,
                ),
            )
        }
    }

    private fun observeHiddenTracks() {
        viewModelScope.launch {
            getHiddenAudioFilesUseCase().collectLatest { hiddenTracks ->
                _uiState.update { state -> state.copy(hiddenTracks = hiddenTracks) }
            }
        }
    }

    private fun toggleShowHidden(showHidden: Boolean) {
        _uiState.update { state -> state.copy(showHiddenTracks = showHidden) }
    }

    private fun toggleFavorite(trackId: String, favorite: Boolean) {
        viewModelScope.launch {
            setAudioFavoriteUseCase(trackId, favorite)
        }
    }

    private fun hideTrack(trackId: String) {
        viewModelScope.launch {
            setAudioHiddenUseCase(trackId, true)
        }
    }

    private fun restoreTrack(trackId: String) {
        viewModelScope.launch {
            setAudioHiddenUseCase(trackId, false)
        }
    }

    private fun editMetadata(trackId: String, metadata: AudioMetadataPatch) {
        viewModelScope.launch {
            setAudioMetadataUseCase(trackId, metadata)
        }
    }
}
