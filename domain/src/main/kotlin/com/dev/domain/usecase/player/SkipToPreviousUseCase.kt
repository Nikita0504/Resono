package com.dev.domain.usecase.player

import com.dev.domain.repository.PlayerRepository

class SkipToPreviousUseCase(
    private val playerRepository: PlayerRepository,
) {

    suspend operator fun invoke() {
        playerRepository.skipToPrevious()
    }
}
