package com.dev.domain.usecase.player

import com.dev.domain.model.WaveformData
import com.dev.domain.repository.PlaybackVisualizerRepository
import kotlinx.coroutines.flow.Flow

class ObserveCurrentWaveformUseCase(
    private val playbackVisualizerRepository: PlaybackVisualizerRepository,
) {

    operator fun invoke(): Flow<WaveformData?> {
        return playbackVisualizerRepository.observeCurrentWaveform()
    }
}
