package com.dev.domain.usecase.player

import com.dev.domain.repository.PlayerRepository

class PauseMediaUseCase(
    private val playerRepository: PlayerRepository,
) {

    suspend operator fun invoke() {
        playerRepository.pause()
    }
}
