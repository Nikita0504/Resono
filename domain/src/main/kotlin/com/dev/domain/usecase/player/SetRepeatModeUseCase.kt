package com.dev.domain.usecase.player

import com.dev.domain.model.RepeatMode
import com.dev.domain.repository.PlayerRepository

class SetRepeatModeUseCase(
    private val playerRepository: PlayerRepository,
) {

    suspend operator fun invoke(mode: RepeatMode) {
        playerRepository.setRepeatMode(mode)
    }
}
