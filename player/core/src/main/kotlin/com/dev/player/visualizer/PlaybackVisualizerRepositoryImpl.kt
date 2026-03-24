package com.dev.player.visualizer

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.dev.domain.model.WaveformData
import com.dev.domain.repository.PlaybackVisualizerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

@UnstableApi
class PlaybackVisualizerRepositoryImpl(
    private val exoPlayer: ExoPlayer,
    private val visualizerTap: Media3PlaybackVisualizerTap,
) : PlaybackVisualizerRepository {

    override fun observeCurrentWaveform(): Flow<WaveformData?> {
        val currentMediaIdFlow = callbackFlow {
            val listener = object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                    trySend(mediaItem?.mediaId)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_IDLE) {
                        visualizerTap.clear()
                    }
                    trySend(exoPlayer.currentMediaItem?.mediaId)
                }
            }

            exoPlayer.addListener(listener)
            trySend(exoPlayer.currentMediaItem?.mediaId)
            awaitClose { exoPlayer.removeListener(listener) }
        }.distinctUntilChanged()

        return combine(currentMediaIdFlow, visualizerTap.samples) { mediaId, samples ->
            if (mediaId == null || samples.isEmpty()) {
                null
            } else {
                WaveformData(
                    mediaId = mediaId,
                    samples = samples,
                    source = WaveformData.Source.LIVE,
                    sampleRateHz = Media3PlaybackVisualizerTap.BARS_PER_SECOND,
                )
            }
        }.distinctUntilChanged()
    }
}
