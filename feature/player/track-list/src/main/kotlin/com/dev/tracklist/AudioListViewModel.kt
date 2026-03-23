package com.dev.tracklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.domain.model.MediaFile
import com.dev.domain.usecase.media.GetPhotosUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AudioListViewModel(
    getPhotosUseCase: GetPhotosUseCase,
) : ViewModel() {

    val photos: StateFlow<List<MediaFile.Photo>> = getPhotosUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
