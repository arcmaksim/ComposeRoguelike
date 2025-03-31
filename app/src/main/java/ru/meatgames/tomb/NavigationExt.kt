package ru.meatgames.tomb

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun NavOptionsBuilder.popUp(
    navController: NavController,
) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        inclusive = true
    }
}

fun NavOptionsBuilder.popUpToRoot() {
    popUpTo(Scene.Root.id) {
        inclusive = false
    }
}
