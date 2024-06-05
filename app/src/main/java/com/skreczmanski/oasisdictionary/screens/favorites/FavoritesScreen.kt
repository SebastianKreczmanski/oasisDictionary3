package com.skreczmanski.oasisdictionary.screens.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skreczmanski.oasisdictionary.R
import com.skreczmanski.oasisdictionary.entities.FavoriteWord
import com.skreczmanski.oasisdictionary.entities.PolAngEntry
import com.skreczmanski.oasisdictionary.viewmodel.LogoViewModel
import com.skreczmanski.oasisdictionary.viewmodel.PolAngViewModel

@Composable
fun FavoritesScreen(navController: NavHostController, logoViewModel: LogoViewModel, polAngViewModel: PolAngViewModel) {
    val tloPaskaNawigacji = painterResource(id = R.drawable.pasek_gora_sahara2)
    val jasneTlo = Color(0xFFFDF5E6) // Bardzo jasny odcień kremowego, prawie biały

    val logoPosition = logoViewModel.logoPosition.value
    val logoSize = logoViewModel.logoSize.value

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current

    val endPadding = with(density) {
        screenWidth - logoPosition.x.toDp() - logoSize + 40.dp
    }

    val favoriteWordsState = polAngViewModel.ulubione.collectAsState()

    Column {

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
                                text = stringResource(id = R.string.favorites),
                                fontSize = 24.sp, // Rozmiar czcionki
                                fontWeight = FontWeight.Bold, // Pogrubiona czcionka
                                modifier = Modifier.alignByBaseline() // Wyrównanie do linii bazowej
                            )
                        }
                    }
                }
                if (favoriteWordsState.value.isEmpty()) {
                    // Wyświetl kartę z komunikatem o braku ulubionych słów
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = 4.dp,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_favorites_message),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black, // Ciemniejsza czcionka
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    // Wyświetl tabelę ulubionych słów
                    FavoritesTable(polAngViewModel)
                }
            }

        }

    }

}

@Composable
fun FavoritesTable(viewModel: PolAngViewModel) {
    val favoriteWordsState = viewModel.ulubione.collectAsState()
    val sortedWords = favoriteWordsState.value
        .sortedBy { it.word.first() }
        .groupBy { it.word.first().uppercaseChar() }

    LazyColumn {
        sortedWords.forEach { (initial, words) ->
            item {
                Text(
                    text = initial.toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black, // Ciemna czcionka
                    fontSize = 20.sp, // Możesz dostosować rozmiar czcionki
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally) // Wyśrodkowanie tekstu
                )
            }
            words.forEachIndexed { index, wordItem ->
                item {
                    FavoriteWordRow(wordItem, index % 2 == 0, viewModel)
                    Divider(color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun FavoriteWordRow(wordItem: FavoriteWord, isEven: Boolean, viewModel: PolAngViewModel) {
    val backgroundColor = if (isEven) Color(0xFFE0E0E0) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = wordItem.word,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = wordItem.translation,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
        IconButton(onClick = {
            viewModel.deleteFromFavorites(PolAngEntry(wordItem.id, wordItem.word, wordItem.translation))
        }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
        }
    }
}