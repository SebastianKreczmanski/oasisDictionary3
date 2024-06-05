package com.skreczmanski.oasisdictionary.screens.info

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.Card
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skreczmanski.oasisdictionary.R
import com.skreczmanski.oasisdictionary.viewmodel.LogoViewModel

enum class ScreenType {
    MAIN, WSTEP, POMYSL, TWORCY, KONTAKT
}

@Composable
fun InfoScreen(navController: NavHostController, logoViewModel: LogoViewModel) {
    var currentScreen by remember { mutableStateOf(ScreenType.MAIN) }

    when (currentScreen) {
        ScreenType.MAIN -> MainScreen(navController, logoViewModel) { screen ->
            currentScreen = screen
        }
        ScreenType.WSTEP -> DetailScreen(navController, logoViewModel, ScreenType.WSTEP) { screen ->
            currentScreen = screen
        }
        ScreenType.POMYSL -> DetailScreen(navController, logoViewModel, ScreenType.POMYSL) { screen ->
            currentScreen = screen
        }
        ScreenType.TWORCY -> DetailScreen(navController, logoViewModel, ScreenType.TWORCY) { screen ->
            currentScreen = screen
        }
        ScreenType.KONTAKT -> DetailScreen(navController, logoViewModel, ScreenType.KONTAKT) { screen ->
            currentScreen = screen
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController, logoViewModel: LogoViewModel, onScreenSelected: (ScreenType) -> Unit) {
    val logoPosition = logoViewModel.logoPosition.value
    val logoSize = logoViewModel.logoSize.value

    val tloPaskaNawigacji = painterResource(id = R.drawable.pasek_gora_sahara2)
    val jasneTlo = Color(0xFFFDF5E6) // Bardzo jasny odcień kremowego, prawie biały

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current

    val endPadding = with(density) {
        screenWidth - logoPosition.x.toDp() - logoSize + 40.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(jasneTlo)
    ) {
        Column {
            // Pasek nawigacji z obrazkiem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Wysokość paska nawigacji
            ) {
                Image(
                    painter = tloPaskaNawigacji,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp) // Rozciągnięcie obrazka na całą wysokość
                        .shadow(4.dp, shape = RectangleShape) // Dodanie cienia
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Pierwszy rząd z powiększoną strzałką powrotu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(30.dp)
                            ) // Powiększona ikona
                        }
                    }

                    // Drugi rząd z napisem "Informacje"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = endPadding), // Używamy obliczonego odstępu
                        horizontalArrangement = Arrangement.End // Wyrównanie do prawej
                    ) {
                        Text(
                            text = stringResource(id = R.string.info),
                            fontSize = 24.sp, // Rozmiar czcionki
                            fontWeight = FontWeight.Bold, // Pogrubiona czcionka
                            modifier = Modifier.alignByBaseline() // Wyrównanie do linii bazowej
                        )
                    }
                }
            }

            val buttonsData = listOf(
                ButtonData(R.drawable.description1, R.string.wstep_naglowek, R.string.wstep_opis, ScreenType.WSTEP),
                ButtonData(R.drawable.point, R.string.pomysl_naglowek, R.string.pomysl_opis, ScreenType.POMYSL),
                ButtonData(R.drawable.kaktusy, R.string.tworcy_naglowek, R.string.tworcy_opis, ScreenType.TWORCY),
                ButtonData(R.drawable.kopertka, R.string.kontakt_naglowek, R.string.kontakt_opis, ScreenType.KONTAKT)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                buttonsData.forEach { data ->
                    InfoButton(data, onScreenSelected)

                }
            }
        }
    }
}

@Composable
fun DetailScreen(navController: NavHostController, logoViewModel: LogoViewModel, screenType: ScreenType, onScreenSelected: (ScreenType) -> Unit) {
    val jasneTlo = Color(0xFFFDF5E6) // Bardzo jasny odcień kremowego, prawie biały
    val tloPaskaNawigacji = painterResource(id = R.drawable.pasek_gora_sahara2)
    val endPadding = calculateEndPadding(logoViewModel) // Funkcja do obliczenia endPadding

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(jasneTlo)
    ) {
        Column {
            // Pasek nawigacji z obrazkiem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    painter = tloPaskaNawigacji,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .shadow(4.dp, shape = RectangleShape)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = endPadding),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = stringResource(id = R.string.info),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                }
            }

            // Przyciski nawigacyjne
            NavigationButtons(screenType, onScreenSelected)

            // Zawartość szczegółowa ekranu
            DetailContent(screenType)
        }
    }
}

@Composable
fun NavigationButtons(currentScreen: ScreenType, onScreenSelected: (ScreenType) -> Unit) {
    val buttonSize = 65.dp
    val activeButtonSize = 85.dp
    val darkTextColor = Color(0xFF333333) // Przykładowy ciemniejszy kolor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ScreenType.values().forEach { screenType ->
            val iconResId = getIconForScreenType(screenType)
            val titleResId = getTitleForScreenType(screenType)
            if (iconResId != null) {
                val isSelected = currentScreen == screenType
                val size = if (isSelected) activeButtonSize else buttonSize

                Button(
                    onClick = { onScreenSelected(screenType) },
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = stringResource(id = getTitleForScreenType(screenType)),
                        modifier = Modifier.fillMaxSize()
                    )
                    //Spacer(modifier = Modifier.height(4.dp)) // Dodaj odstęp między ikoną a tekstem
                    Text(
                        text = stringResource(id = titleResId),
                        fontSize = 12.sp, // Dostosuj rozmiar czcionki
                        textAlign = TextAlign.Center,
                        color = darkTextColor
                    )
                }
            }
        }
    }
}

fun getIconForScreenType(screenType: ScreenType): Int? {
    return when (screenType) {
        ScreenType.WSTEP -> R.drawable.description1
        ScreenType.POMYSL -> R.drawable.point
        ScreenType.TWORCY -> R.drawable.kaktusy
        ScreenType.KONTAKT -> R.drawable.kopertka
        else -> null
    }
}
fun getTitleForScreenType(screenType: ScreenType): Int {
    return when (screenType) {
        ScreenType.WSTEP -> R.string.wstep_naglowek
        ScreenType.POMYSL -> R.string.pomysl_naglowek
        ScreenType.TWORCY -> R.string.tworcy_naglowek
        ScreenType.KONTAKT -> R.string.kontakt_naglowek

        else -> R.string.app_name
    }
}

fun getOpisForScreenType(screenType: ScreenType): Int {
    return when (screenType) {
        ScreenType.WSTEP -> R.string.wstep_opis
        ScreenType.POMYSL -> R.string.pomysl_opis
        ScreenType.TWORCY -> R.string.tworcy_opis
        ScreenType.KONTAKT -> R.string.kontakt_opis

        else -> R.string.app_name
    }
}

@Composable
fun DetailContent(screenType: ScreenType) {
    val darkTextColor = Color(0xFF333333) // Przykładowy ciemniejszy kolor
    val lightTextColor = Color(0xFF666666) // Przykładowy jaśniejszy kolor

    val titleRes = when (screenType) {
        ScreenType.WSTEP -> R.string.wstep_naglowek
        ScreenType.POMYSL -> R.string.pomysl_naglowek
        ScreenType.TWORCY -> R.string.tworcy_naglowek
        ScreenType.KONTAKT -> R.string.kontakt_naglowek
        else -> R.string.app_name
    }
    val contentRes = when (screenType) {
        ScreenType.WSTEP -> R.string.wstep_tresc
        ScreenType.POMYSL -> R.string.pomysl_tresc
        ScreenType.TWORCY -> R.string.tworcy_tresc
        ScreenType.KONTAKT -> R.string.kontakt_tresc
        else -> R.string.app_name
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = stringResource(id = titleRes),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = darkTextColor // Użyj zdefiniowanego koloru
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Text(
                text = stringResource(id = contentRes),
                color = lightTextColor // Użyj zdefiniowanego koloru
            )
        }
    }
}

@Composable
fun calculateEndPadding(logoViewModel: LogoViewModel): Dp {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val logoPosition = logoViewModel.logoPosition.value
    val logoSize = logoViewModel.logoSize.value

    return with(LocalDensity.current) {
        screenWidth - logoPosition.x.toDp() - logoSize + 40.dp
    }
}

data class ButtonData(
    @DrawableRes val imageRes: Int,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    val screenType: ScreenType
)

@Composable
fun InfoButton(data: ButtonData, onScreenSelected: (ScreenType) -> Unit) {
    val lightTextColor = Color(0xFF666666) // Przykładowy jaśniejszy kolor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onScreenSelected(data.screenType) },
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(id = data.nameRes),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = stringResource(id = data.descriptionRes),
                    color = lightTextColor
                )
            }
        }
    }
}
