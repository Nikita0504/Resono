package com.dev.domain.repository

import com.dev.domain.model.WaveformData
import kotlinx.coroutines.flow.Flow

interface WaveformRepository {
    fun observeWaveform(mediaId: String): Flow<WaveformData?>
}
