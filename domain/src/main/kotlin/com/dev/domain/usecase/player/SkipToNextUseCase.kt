package com.dev.domain.usecase.player

import com.dev.domain.repository.PlayerRepository

class SkipToNextUseCase(
    private val playerRepository: PlayerRepository,
) {

    suspend operator fun invoke() {
        playerRepository.skipToNext()
    }
}
