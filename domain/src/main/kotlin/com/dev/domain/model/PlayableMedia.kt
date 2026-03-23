package com.dev.domain.model
sealed interface PlayableMedia {
    val mediaFile: MediaFile
    
    data class Audio(
        val file: MediaFile.Audio,
    ) : PlayableMedia {
        override val mediaFile: MediaFile = file
    }
    
    data class Video(
        val file: MediaFile.Video,
    ) : PlayableMedia {
        override val mediaFile: MediaFile = file
    }
}
