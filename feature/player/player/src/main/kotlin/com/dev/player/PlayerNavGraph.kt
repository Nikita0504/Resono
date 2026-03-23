package com.dev.player

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object PlayerGraph

@Serializable
internal object PlayerHomeRoute

fun NavGraphBuilder.playerNavGraph() {
    navigation<PlayerGraph>(startDestination = PlayerHomeRoute) {
        composable<PlayerHomeRoute> {
            PlayerRoute {
                PlayerScreen()
            }
        }
    }
}

@Composable
private fun PlayerRoute(content: @Composable () -> Unit) {
    content()
}
