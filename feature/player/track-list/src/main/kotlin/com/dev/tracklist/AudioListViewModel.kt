package com.dev.tracklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.domain.model.MediaFile
import com.dev.domain.usecase.media.GetAudioFilesUseCase
import com.dev.domain.usecase.media.ScanLocalMediaUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collectLatest

class AudioListViewModel(
    private val getAudioFilesUseCase: GetAudioFilesUseCase,
    private val scanLocalMediaUseCase: ScanLocalMediaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackListUiState())
    val uiState: StateFlow<TrackListUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<TrackListEffect>()
    val effects: SharedFlow<TrackListEffect> = _effects.asSharedFlow()

    init {
        observeTracks()
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
}
