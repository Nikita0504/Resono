package com.dev.data.repository

import com.dev.domain.model.WaveformData
import com.dev.domain.repository.WaveformRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WaveformRepositoryImpl : WaveformRepository {

    override fun observeWaveform(mediaId: String): Flow<WaveformData?> {
        // Extension point: live waveform будет приходить из :player:core (Media3 WaveformAudioBufferSink),
        // precomputed waveform — отдельная фоновая обработка и persist-слой.
        return flowOf(null)
    }
}
