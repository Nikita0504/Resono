package com.dev.player.visualizer

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.audio.TeeAudioProcessor
import androidx.media3.exoplayer.audio.WaveformAudioBufferSink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.max

@UnstableApi
class Media3PlaybackVisualizerTap {

    private val historyLock = Any()
    private val history = ArrayDeque<Float>(MAX_SAMPLES)
    private val _samples = MutableStateFlow<List<Float>>(emptyList())
    val samples: StateFlow<List<Float>> = _samples.asStateFlow()

    private val sink = WaveformAudioBufferSink(
        BARS_PER_SECOND,
        OUTPUT_CHANNELS,
    ) { _, waveformBar ->
        val sample = waveformBar.toUiSample()
        val snapshot = synchronized(historyLock) {
            if (history.size >= MAX_SAMPLES) {
                history.removeFirst()
            }
            history.addLast(sample)
            history.toList()
        }
        _samples.value = snapshot
    }

    val audioProcessor: TeeAudioProcessor = TeeAudioProcessor(sink)

    fun clear() {
        synchronized(historyLock) { history.clear() }
        _samples.value = emptyList()
    }

    private fun WaveformAudioBufferSink.WaveformBar.toUiSample(): Float {
        val rms = getRootMeanSquare().toFloat().coerceIn(0f, 1f)
        val peak = max(
            abs(getMinSampleValue().toFloat()),
            abs(getMaxSampleValue().toFloat()),
        ).coerceIn(0f, 1f)
        return (rms * 0.7f + peak * 0.3f).coerceIn(0f, 1f)
    }

    companion object {
        const val BARS_PER_SECOND: Int = 40
        private const val OUTPUT_CHANNELS: Int = 1
        private const val MAX_SAMPLES: Int = 96
    }
}
