package com.dev.domain.model

data class AudioAlbumDraft(
    val title: String,
    val description: String? = null,
    val artworkUri: String? = null,
    val trackIds: List<String> = emptyList(),
)
