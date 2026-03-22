package com.dev.domain.usecase.player

import com.dev.domain.model.PlayableMedia
import com.dev.domain.repository.PlayerRepository

class PlayMediaUseCase(
    private val playerRepository: PlayerRepository,
) {

    suspend operator fun invoke(files: List<PlayableMedia>, startIndex: Int = 0) {
        playerRepository.prepare(files, startIndex)
        playerRepository.play()
    }
}
