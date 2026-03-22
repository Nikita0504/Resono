package com.dev.domain.usecase.player

import com.dev.domain.repository.PlayerRepository

class SeekToPositionUseCase(
    private val playerRepository: PlayerRepository,
) {

    suspend operator fun invoke(positionMs: Long) {
        playerRepository.seekTo(positionMs)
    }
}
