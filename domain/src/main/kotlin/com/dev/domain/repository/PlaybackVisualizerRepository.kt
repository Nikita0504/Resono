package com.dev.domain.repository

import com.dev.domain.model.WaveformData
import kotlinx.coroutines.flow.Flow

interface PlaybackVisualizerRepository {
    fun observeCurrentWaveform(): Flow<WaveformData?>
}
