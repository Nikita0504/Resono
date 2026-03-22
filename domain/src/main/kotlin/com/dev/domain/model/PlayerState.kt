package com.dev.domain.model

enum class PlaybackState {
    IDLE,
    BUFFERING,
    READY,
    ENDED
}

sealed class PlaybackError {
    data class Network(val message: String) : PlaybackError()
    data class Decoding(val message: String) : PlaybackError()
    data class FileNotFound(val uri: String) : PlaybackError()
    data class Unknown(val message: String) : PlaybackError()

    companion object {
        fun fromException(errorCode: Int, message: String?): PlaybackError = when (errorCode) {
            10003, 10002 -> Network(message ?: "Network error")
            10010, 10011 -> Decoding(message ?: "Decoding error")
            10004 -> FileNotFound(message ?: "File not found")
            else -> Unknown(message ?: "Unknown error")
        }
    }
}

enum class RepeatMode {
    OFF,
    ONE,
    ALL
}

data class PlayerState(
    val currentItem: PlayableMedia? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isShuffleEnabled: Boolean = false,
    val playbackError: PlaybackError? = null,
    val playlist: List<PlayableMedia> = emptyList(),
    val currentIndex: Int = 0,
) {
    val progress: Float
        get() = if (durationMs > 0) positionMs / durationMs.toFloat() else 0f

    val isBuffering: Boolean
        get() = playbackState == PlaybackState.BUFFERING

    val hasError: Boolean
        get() = playbackError != null

    val isReady: Boolean
        get() = playbackState == PlaybackState.READY

    val hasNext: Boolean
        get() = currentIndex < playlist.size - 1
}
