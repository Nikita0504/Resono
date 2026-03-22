package com.dev.player.mapper

import androidx.media3.common.Player
import com.dev.domain.model.RepeatMode

fun RepeatMode.toMedia3RepeatMode(): Int = when (this) {
    RepeatMode.ONE -> Player.REPEAT_MODE_ONE
    RepeatMode.ALL -> Player.REPEAT_MODE_ALL
    RepeatMode.OFF -> Player.REPEAT_MODE_OFF
}

fun Int.toDomainRepeatMode(): RepeatMode = when (this) {
    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
    Player.REPEAT_MODE_OFF -> RepeatMode.OFF
    else -> RepeatMode.OFF
}