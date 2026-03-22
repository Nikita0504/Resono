package com.dev.domain.usecase.player

import com.dev.domain.model.PlayerState
import com.dev.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

class ObservePlayerStateUseCase(
    private val playerRepository: PlayerRepository,
) {

    operator fun invoke(): Flow<PlayerState> {
        return playerRepository.playerState
    }
}
