package com.dev.domain.repository

import com.dev.domain.model.PlayableMedia
import com.dev.domain.model.PlayerState
import com.dev.domain.model.RepeatMode
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {

    val playerState: Flow<PlayerState>

    suspend fun prepare(files: List<PlayableMedia>, startIndex: Int = 0)

    suspend fun play()

    suspend fun pause()

    suspend fun seekTo(positionMs: Long)

    suspend fun seekToDefaultPosition(index: Int)

    suspend fun setRepeatMode(mode: RepeatMode)

    suspend fun setShuffleEnabled(enabled: Boolean)

    suspend fun skipToNext()

    suspend fun skipToPrevious()

    suspend fun clear()
}
