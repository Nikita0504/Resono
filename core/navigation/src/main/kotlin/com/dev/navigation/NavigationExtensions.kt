package com.dev.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun NavController.navigateSingleTop(route: Any) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateSingleTop(route: Any, builder: NavOptionsBuilder.() -> Unit) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        builder()
    }
}
