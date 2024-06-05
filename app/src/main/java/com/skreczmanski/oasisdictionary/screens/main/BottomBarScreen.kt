package com.skreczmanski.oasisdictionary.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Addword : BottomBarScreen(
        route = "addword",
        title = "Add",
        icon = Icons.Default.AddCircle
    )
    object Favorites : BottomBarScreen(
        route = "favorites",
        title = "Favorites",
        icon = Icons.Default.Favorite
    )
    object Language : BottomBarScreen(
        route = "language",
        title = "Language",
        icon = Icons.Default.Face
    )
    object Info : BottomBarScreen(
        route = "info",
        title = "Info",
        icon = Icons.Default.Info
    )
    object Settings : BottomBarScreen(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
}