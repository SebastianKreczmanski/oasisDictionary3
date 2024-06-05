package com.skreczmanski.oasisdictionary.graphs

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.skreczmanski.oasisdictionary.screens.addword.AddwordScreen
import com.skreczmanski.oasisdictionary.screens.favorites.FavoritesScreen
import com.skreczmanski.oasisdictionary.screens.info.InfoScreen
import com.skreczmanski.oasisdictionary.screens.main.BottomBarScreen
import com.skreczmanski.oasisdictionary.screens.settings.SettingsScreen
import com.skreczmanski.oasisdictionary.screens.translations.LanguageScreen
import com.skreczmanski.oasisdictionary.screens.translations.PolEng.EngPolDictionary
import com.skreczmanski.oasisdictionary.screens.translations.PolEng.PolEngDictionary
import com.skreczmanski.oasisdictionary.viewmodel.LogoViewModel
import com.skreczmanski.oasisdictionary.viewmodel.PolAngViewModel

@Composable
fun BottomNavGraph(navController: NavHostController, viewModelFactory: ViewModelProvider.Factory) {
    val logoViewModel = viewModel<LogoViewModel>()
    val polAngViewModel = viewModel<PolAngViewModel>(factory = viewModelFactory)

    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Language.route
    ) {
        composable(route = BottomBarScreen.Addword.route) {
            AddwordScreen(navController, logoViewModel, polAngViewModel)
        }
        composable(route = BottomBarScreen.Favorites.route) {
            FavoritesScreen(navController, logoViewModel, polAngViewModel)
        }
        composable(route = BottomBarScreen.Language.route) {
            LanguageScreen(navController, logoViewModel)
        }
        composable(route = BottomBarScreen.Info.route) {
            InfoScreen(navController, logoViewModel)
        }
        composable(route = BottomBarScreen.Settings.route) {
            SettingsScreen(navController, logoViewModel, polAngViewModel)
        }

        composable(route = "polEngDictionary") {
            PolEngDictionary(navController, polAngViewModel)
        }
        composable(route = "engPolDictionary") {
            EngPolDictionary(navController, polAngViewModel)
        }
    }
}