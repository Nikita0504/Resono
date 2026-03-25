package com.dev.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.dev.domain.model.PlaybackError
import com.dev.domain.model.PlaybackState
import com.dev.domain.model.PlayableMedia
import com.dev.domain.model.PlayerState
import com.dev.domain.model.RepeatMode
import com.dev.domain.repository.PlayerRepository
import com.dev.logger.Logger
import com.dev.logger.d
import com.dev.logger.e
import com.dev.logger.i
import com.dev.logger.w
import com.dev.player.mapper.MediaItemMapper
import com.dev.player.mapper.toDomainRepeatMode
import com.dev.player.mapper.toMedia3RepeatMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl(
    private val player: ExoPlayer,
    private val logger: Logger,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : PlayerRepository {
    private companion object {
        const val PROGRESS_TICK_MS = 300L
    }

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)

    private var currentPlaylist: Map<String, PlayableMedia> = emptyMap()
    private var progressTickerJob: Job? = null

    private val listener = object : Player.Listener {

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateState()
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int,
        ) {
            updateState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateState()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                startProgressTicker()
            } else {
                stopProgressTicker()
            }
            _playerState.update { it.copy(isPlaying = isPlaying) }
            updateState()
        }

        override fun onPlayerError(error: PlaybackException) {
            val currentFile = currentMediaFile()
            logger.e("PlayerRepo", "Playback error for ${currentFile?.mediaFile?.name}", error)
            _playerState.update {
                it.copy(playbackError = PlaybackError.fromException(error.errorCode, error.message))
            }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            updateState()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _playerState.update { it.copy(repeatMode = repeatMode.toDomainRepeatMode()) }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _playerState.update { it.copy(isShuffleEnabled = shuffleModeEnabled) }
        }
    }

    init {
        player.addListener(listener)
        updateState()
        logger.i("PlayerRepo", "Player initialized")
    }

    override suspend fun prepare(files: List<PlayableMedia>, startIndex: Int) {
        withContext(mainDispatcher) {
            currentPlaylist = files.associateBy { it.mediaFile.id }

            val mediaItems = files.mapNotNull { playable ->
                with(MediaItemMapper) {
                    playable.toMediaItem().also { item ->
                        if (item == null) {
                            logger.w("PlayerRepo", "Skipping ${playable.mediaFile.name}: no URI")
                        }
                    }
                }
            }

            if (mediaItems.isEmpty()) {
                logger.e("PlayerRepo", "No playable items in playlist", IllegalStateException("No playable items"))
                return@withContext
            }

            player.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
            player.prepare()
            updateState()
            logger.i("PlayerRepo", "Prepared ${mediaItems.size} items, starting at $startIndex")
        }
    }

    override suspend fun play() {
        withContext(mainDispatcher) {
            player.play()
            startProgressTicker()
            updateState()
            logger.d("PlayerRepo", "Play called")
        }
    }

    override suspend fun pause() {
        withContext(mainDispatcher) {
            player.pause()
            stopProgressTicker()
            updateState()
            logger.d("PlayerRepo", "Pause called")
        }
    }

    override suspend fun seekTo(positionMs: Long) {
        withContext(mainDispatcher) {
            player.seekTo(positionMs)
            updateState()
            logger.d("PlayerRepo", "Seek to $positionMs ms")
        }
    }

    override suspend fun seekToDefaultPosition(index: Int) {
        withContext(mainDispatcher) {
            player.seekToDefaultPosition(index)
            updateState()
            logger.d("PlayerRepo", "Seek to default position $index")
        }
    }

    override suspend fun setRepeatMode(mode: RepeatMode) {
        withContext(mainDispatcher) {
            player.repeatMode = mode.toMedia3RepeatMode()
            updateState()
            logger.d("PlayerRepo", "Set repeat mode: $mode")
        }
    }

    override suspend fun setShuffleEnabled(enabled: Boolean) {
        withContext(mainDispatcher) {
            player.shuffleModeEnabled = enabled
            updateState()
            logger.d("PlayerRepo", "Set shuffle enabled: $enabled")
        }
    }

    override suspend fun skipToNext() {
        withContext(mainDispatcher) {
            if (player.hasNextMediaItem()) {
                player.seekToNextMediaItem()
                logger.d("PlayerRepo", "Skipped to next")
            }
            updateState()
        }
    }

    override suspend fun skipToPrevious() {
        withContext(mainDispatcher) {
            if (player.hasPreviousMediaItem()) {
                player.seekToPreviousMediaItem()
                logger.d("PlayerRepo", "Skipped to previous")
            }
            updateState()
        }
    }

    override suspend fun clear() {
        withContext(mainDispatcher) {
            stopProgressTicker()
            player.clearMediaItems()
            currentPlaylist = emptyMap()
            _playerState.value = PlayerState()
            logger.d("PlayerRepo", "Player cleared")
        }
    }

    private fun currentMediaFile(): PlayableMedia? =
        player.currentMediaItem?.mediaId?.let { currentPlaylist[it] }

    private fun updateState() {
        val currentFile = currentMediaFile()
        val playlist = (0 until player.mediaItemCount)
            .mapNotNull { i -> player.getMediaItemAt(i).mediaId.let { currentPlaylist[it] } }

        _playerState.update { state ->
            state.copy(
                currentItem = currentFile,
                playlist = playlist,
                currentIndex = player.currentMediaItemIndex.coerceAtLeast(0),
                isPlaying = player.isPlaying,
                positionMs = player.currentPosition.coerceAtLeast(0L),
                durationMs = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L,
                playbackState = player.playbackState.toPlayerPlaybackState(),
                repeatMode = player.repeatMode.toDomainRepeatMode(),
                isShuffleEnabled = player.shuffleModeEnabled,
            )
        }
    }

    private fun startProgressTicker() {
        if (progressTickerJob?.isActive == true) return
        progressTickerJob = scope.launch {
            while (isActive && player.isPlaying) {
                _playerState.update { state ->
                    state.copy(
                        positionMs = player.currentPosition.coerceAtLeast(0L),
                        durationMs = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L,
                    )
                }
                delay(PROGRESS_TICK_MS)
            }
        }
    }

    private fun stopProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = null
    }

    private fun Int.toPlayerPlaybackState(): PlaybackState = when (this) {
        Player.STATE_IDLE -> PlaybackState.IDLE
        Player.STATE_BUFFERING -> PlaybackState.BUFFERING
        Player.STATE_READY -> PlaybackState.READY
        Player.STATE_ENDED -> PlaybackState.ENDED
        else -> PlaybackState.IDLE
    }

    fun release() {
        stopProgressTicker()
        player.removeListener(listener)
        player.release()
        logger.i("PlayerRepo", "Player released")
    }
}
