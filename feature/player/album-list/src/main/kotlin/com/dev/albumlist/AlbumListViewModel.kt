package com.dev.albumlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.domain.usecase.media.DeleteAlbumUseCase
import com.dev.domain.usecase.media.GetAlbumByIdUseCase
import com.dev.domain.usecase.media.GetAlbumsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumListViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getAlbumByIdUseCase: GetAlbumByIdUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumListUiState())
    val uiState: StateFlow<AlbumListUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<AlbumListEffect>()
    val effects: SharedFlow<AlbumListEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            getAlbumsUseCase().collectLatest { albums ->
                _uiState.update { it.copy(albums = albums) }
            }
        }
    }

    fun onIntent(intent: AlbumListIntent) {
        when (intent) {
            AlbumListIntent.CreateAlbum -> {
                viewModelScope.launch { _effects.emit(AlbumListEffect.OpenCreateAlbum) }
            }

            is AlbumListIntent.OpenAlbum -> {
                viewModelScope.launch {
                    _effects.emit(AlbumListEffect.OpenAlbumEditor(intent.albumId))
                }
            }

            is AlbumListIntent.PlayAlbum -> {
                viewModelScope.launch {
                    val album = getAlbumByIdUseCase(intent.albumId).first()
                    val tracks = album?.tracks.orEmpty()
                    if (tracks.isNotEmpty()) {
                        _effects.emit(AlbumListEffect.PlayAlbum(tracks))
                    }
                }
            }

            is AlbumListIntent.DeleteAlbum -> {
                viewModelScope.launch {
                    deleteAlbumUseCase(intent.albumId)
                }
            }
        }
    }
}
