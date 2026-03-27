package com.dev.albumlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.domain.model.AudioAlbumDraft
import com.dev.domain.usecase.media.CreateAlbumUseCase
import com.dev.domain.usecase.media.DeleteAlbumUseCase
import com.dev.domain.usecase.media.GetAlbumByIdUseCase
import com.dev.domain.usecase.media.GetAudioFilesUseCase
import com.dev.domain.usecase.media.UpdateAlbumUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val getAlbumByIdUseCase: GetAlbumByIdUseCase,
    private val getAudioFilesUseCase: GetAudioFilesUseCase,
    private val createAlbumUseCase: CreateAlbumUseCase,
    private val updateAlbumUseCase: UpdateAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
) : ViewModel() {

    private val albumId: String? = savedStateHandle["albumId"]

    private val _uiState = MutableStateFlow(AlbumEditorUiState(albumId = albumId))
    val uiState: StateFlow<AlbumEditorUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<AlbumEditorEffect>()
    val effects: SharedFlow<AlbumEditorEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            getAudioFilesUseCase().collectLatest { tracks ->
                _uiState.update { it.copy(allTracks = tracks) }
            }
        }
        if (albumId != null) {
            viewModelScope.launch {
                getAlbumByIdUseCase(albumId).collectLatest { album ->
                    if (album != null) {
                        _uiState.update {
                            it.copy(
                                existingAlbum = album,
                                title = album.title,
                                description = album.description.orEmpty(),
                                artworkUri = album.artworkUri.orEmpty(),
                                selectedTrackIds = album.trackIds.toSet(),
                            )
                        }
                    }
                }
            }
        }
    }

    fun onIntent(intent: AlbumEditorIntent) {
        when (intent) {
            is AlbumEditorIntent.TitleChanged -> _uiState.update { it.copy(title = intent.value) }
            is AlbumEditorIntent.DescriptionChanged -> _uiState.update { it.copy(description = intent.value) }
            is AlbumEditorIntent.ArtworkUriChanged -> _uiState.update { it.copy(artworkUri = intent.value) }
            is AlbumEditorIntent.ToggleTrack -> toggleTrack(intent.trackId)
            AlbumEditorIntent.Save -> save()
            AlbumEditorIntent.Delete -> delete()
        }
    }

    private fun toggleTrack(trackId: String) {
        _uiState.update { state ->
            val updated = state.selectedTrackIds.toMutableSet()
            if (!updated.add(trackId)) {
                updated.remove(trackId)
            }
            state.copy(selectedTrackIds = updated)
        }
    }

    private fun save() {
        val state = uiState.value
        val title = state.title.trim()
        if (title.isEmpty()) return

        val draft = AudioAlbumDraft(
            title = title,
            description = state.description.trim().ifEmpty { null },
            artworkUri = state.artworkUri.trim().ifEmpty { null },
            trackIds = state.selectedTrackIds.toList(),
        )

        viewModelScope.launch {
            val existingId = state.existingAlbum?.id
            if (existingId == null) {
                createAlbumUseCase(draft)
            } else {
                updateAlbumUseCase(existingId, draft)
            }
            _effects.emit(AlbumEditorEffect.CloseEditor)
        }
    }

    private fun delete() {
        val existingId = uiState.value.existingAlbum?.id ?: return
        viewModelScope.launch {
            deleteAlbumUseCase(existingId)
            _effects.emit(AlbumEditorEffect.CloseEditor)
        }
    }
}
