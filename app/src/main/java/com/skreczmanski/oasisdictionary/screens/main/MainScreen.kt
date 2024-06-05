package com.skreczmanski.oasisdictionary.screens.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skreczmanski.oasisdictionary.R
import com.skreczmanski.oasisdictionary.graphs.BottomNavGraph
import com.skreczmanski.oasisdictionary.viewmodel.LogoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModelFactory: ViewModelProvider.Factory) {

    val navController = rememberNavController()
    val logoViewModel = viewModel<LogoViewModel>()

    Box {

        Scaffold(
            bottomBar = { BottomBar(navController = navController) }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                BottomNavGraph(navController = navController, viewModelFactory = viewModelFactory)
                AnimatedLogo(navController, logoViewModel)
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Addword,
        BottomBarScreen.Favorites,
        BottomBarScreen.Language,
        BottomBarScreen.Info,
        BottomBarScreen.Settings
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp), // Dodatkowy padding na dole
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomNavigation(
            backgroundColor = Color.Gray.copy(alpha = 0.95f),
            elevation = 5.dp
        ) {
            screens.forEach { screen ->
                if (screen == BottomBarScreen.Language) {
                    OverlaySpecialButton(screen, navController, isSelected = screen.route == currentDestination?.route)
                } else {
                    AddItem(screen, currentDestination, navController)
                }
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    BottomNavigationItem(
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon",
                tint = if (isSelected) Color.White else Color.LightGray
            )
        },
        selected = isSelected,
        selectedContentColor = Color.Black,
        unselectedContentColor = Color.DarkGray,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}

@Composable
fun OverlaySpecialButton(
    screen: BottomBarScreen,
    navController: NavHostController,
    isSelected: Boolean
) {
    FloatingActionButton(
        onClick = { navController.navigate(screen.route) },
        backgroundColor = if (isSelected) Color.DarkGray else Color.LightGray,
        modifier = Modifier.size(56.dp) // Rozmiar przycisku
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = "Language",
            tint = Color.White
        )
    }
}

@Composable
fun AnimatedLogo(navController: NavHostController, logoViewModel: LogoViewModel) {
    val animationState = remember { mutableStateOf<Pair<String?, Boolean>>(null to false) }

    BoxWithConstraints {
        val density = LocalDensity.current
        val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

        val maxWidth = maxWidth
        val logoWidth = 100.dp // Zakładając, że szerokość logo wynosi 100.dp
        val logoWidthPx = with(density) { logoWidth.toPx() }

        // Oblicz środkową pozycję X dla ekranu Language
        val positionLanguageX = (maxWidth - (logoWidth / 2) + 25.dp)
        val positionLanguageY = 150f // Przykładowa pozycja Y dla ekranu Language
        val scaleLanguage = 1f // Przykładowa skala dla ekranu Language

        val positionAddwordX = screenWidthPx - logoWidthPx - 35f // Przykładowa pozycja X dla ekranu Addword
        val positionAddwordY = 20f // Przykładowa pozycja Y dla ekranu Addword
        val scaleAddword = 0.5f // Przykładowa skala dla ekranu Addword

        val positionFavoritesX = screenWidthPx - logoWidthPx - 35f // Przykładowa pozycja X dla ekranu Favorites
        val positionFavoritesY = 20f // Przykładowa pozycja Y dla ekranu Favorites
        val scaleFavorites = 0.5f // Przykładowa skala dla ekranu Favorites

        val positionInfoX = screenWidthPx - logoWidthPx - 35f // Przykładowa pozycja X dla ekranu Info
        val positionInfoY = 20f // Przykładowa pozycja Y dla ekranu Info
        val scaleInfo = 0.5f // Przykładowa skala dla ekranu Info

        val positionSettingsX = screenWidthPx - logoWidthPx - 35f // Przykładowa pozycja X dla ekranu Settings
        val positionSettingsY = 20f // Przykładowa pozycja Y dla ekranu Settings
        val scaleSettings = 0.5f // Przykładowa skala dla ekranu Settings

        // Przykładowe wartości, dostosuj je do swoich potrzeb
        val positionPolEngX = screenWidthPx - logoWidthPx - 35f // Pozycja X dla PolEngDictionary
        val positionPolEngY = 20f // Pozycja Y dla PolEngDictionary
        val scalePolEng = 0.5f // Skala dla PolEngDictionary

        val positionEngPolX = screenWidthPx - logoWidthPx - 35f // Pozycja X dla EngPolDictionary
        val positionEngPolY = 20f // Pozycja Y dla EngPolDictionary
        val scaleEngPol = 0.5f // Skala dla EngPolDictionary

        val logoPositionX = remember { Animatable(5f) }
        val logoPositionY = remember { Animatable(5f) }
        val logoScale = remember { Animatable(1f) }

        //Czas trwania animacji
        val animationSpec = tween<Float>(durationMillis = 500)

        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        LaunchedEffect(currentRoute) {
            animationState.value = currentRoute to false

            when (currentRoute) {
                BottomBarScreen.Addword.route -> {
                    withContext(Dispatchers.Main) {
                        logoPositionX.animateTo(positionAddwordX, animationSpec)
                        logoPositionY.animateTo(positionAddwordY, animationSpec)
                        logoScale.animateTo(scaleAddword, animationSpec)
                    }
                }

                BottomBarScreen.Favorites.route -> {
                    withContext(Dispatchers.Main) {
                        logoPositionX.animateTo(positionFavoritesX, animationSpec)
                        logoPositionY.animateTo(positionFavoritesY, animationSpec)
                        logoScale.animateTo(scaleFavorites, animationSpec)
                    }
                }

                BottomBarScreen.Language.route -> {
                    withContext(Dispatchers.Main) {
                        logoPositionX.animateTo(positionLanguageX.value, animationSpec)
                        logoPositionY.animateTo(positionLanguageY, animationSpec)
                        logoScale.animateTo(scaleLanguage, animationSpec)
                    }
                }

                BottomBarScreen.Info.route -> {
                    withContext(Dispatchers.Main) {
                        logoPositionX.animateTo(positionInfoX, animationSpec)
                        logoPositionY.animateTo(positionInfoY, animationSpec)
                        logoScale.animateTo(scaleInfo, animationSpec)
                    }
                }

                BottomBarScreen.Settings.route -> {
                    withContext(Dispatchers.Main) {
                        logoPositionX.animateTo(positionSettingsX, animationSpec)
                        logoPositionY.animateTo(positionSettingsY, animationSpec)
                        logoScale.animateTo(scaleSettings, animationSpec)
                    }
                }

                "polEngDictionary" -> {
                    withContext(Dispatchers.Main) {
                        logoPositionX.animateTo(positionPolEngX, animationSpec)
                        logoPositionY.animateTo(positionPolEngY, animationSpec)
                        logoScale.animateTo(scalePolEng, animationSpec)
                    }
                }

                "engPolDictionary" -> {
                    withContext(Dispatchers.Main) {
                        logoPositionX.animateTo(positionEngPolX, animationSpec)
                        logoPositionY.animateTo(positionEngPolY, animationSpec)
                        logoScale.animateTo(scaleEngPol, animationSpec)
                    }
                }
            }

            logoViewModel.logoPosition.value = Offset(logoPositionX.value, logoPositionY.value)
            logoViewModel.logoSize.value = logoWidth * logoScale.value

            animationState.value = currentRoute to true
        }

        //Logo, które skalujemy na ekranach
        Image(
            painter = painterResource(R.drawable.foska_2019),
            contentDescription = "Logo",
            modifier = Modifier
                .graphicsLayer(
                    translationX = logoPositionX.value,
                    translationY = logoPositionY.value,
                    scaleX = logoScale.value,
                    scaleY = logoScale.value
                )
        )
    }
}