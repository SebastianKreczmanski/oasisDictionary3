package com.skreczmanski.oasisdictionary.screens.addword

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skreczmanski.oasisdictionary.R
import com.skreczmanski.oasisdictionary.entities.MyWords
import com.skreczmanski.oasisdictionary.viewmodel.LogoViewModel
import com.skreczmanski.oasisdictionary.viewmodel.PolAngViewModel

@Composable
fun AddwordScreen(navController: NavHostController, logoViewModel: LogoViewModel, polAngViewModel: PolAngViewModel) {

    val myWords = polAngViewModel.myWords.collectAsState()

    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog.value = true },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Word")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFDF5E6)) // Bardzo jasny odcień kremowego, prawie biały
                    .padding(paddingValues)
            ) {
                Column {
                    // Pasek nawigacji z obrazkiem
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp) // Wysokość paska nawigacji
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pasek_gora_sahara2),
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
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(30.dp)) // Powiększona ikona
                                }
                            }

                            // Drugi rząd z napisem "Dodaj Słowo"
                            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                            val density = LocalDensity.current
                            val logoPosition = logoViewModel.logoPosition.value
                            val logoSize = logoViewModel.logoSize.value
                            val endPadding = with(density) {
                                screenWidth - logoPosition.x.toDp() - logoSize + 40.dp
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = endPadding), // Używamy obliczonego odstępu
                                horizontalArrangement = Arrangement.End // Wyrównanie do prawej
                            ) {
                                Text(
                                    text = stringResource(id = R.string.add_word),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.alignByBaseline()
                                )
                            }
                        }
                    }
                    if (myWords.value.isEmpty()) {
                        // Wyświetl kartę z komunikatem o braku słów
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
                                    text = stringResource(id = R.string.no_own_words_message),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black, // Ciemniejsza czcionka
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        // Wyświetl listę słów
                        MyWordsTable(myWords.value, polAngViewModel)
                    }
                }

                // Okno dialogowe do dodawania nowego słowa
                if (showDialog.value) {
                    AddWordDialog(viewModel = polAngViewModel, onDismiss = { showDialog.value = false })
                }
            }
        }
    )
}

@Composable
fun AddWordDialog(viewModel: PolAngViewModel, onDismiss: () -> Unit) {
    val word = remember { mutableStateOf("") }
    val translation = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp), // Zaokrąglone rogi
        backgroundColor = MaterialTheme.colors.surface,
        title = {
            Text(
                text = stringResource(id = R.string.dodaj_slowo),
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = word.value,
                    onValueChange = { word.value = it },
                    label = { Text(stringResource(id = R.string.word), color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)) },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = MaterialTheme.colors.background
                    )
                )
                TextField(
                    value = translation.value,
                    onValueChange = { translation.value = it },
                    label = { Text(stringResource(id = R.string.translation), color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)) },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = MaterialTheme.colors.background
                    ),
                    modifier = Modifier.height(100.dp) // Większe pole do wpisywania tłumaczenia
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addMyWord(word.value, translation.value)
                    onDismiss()
                },
                modifier = Modifier
                    //.background(Color(0xFF004D40))
                    .then(Modifier.padding(8.dp))
            ) {
                Text(stringResource(id = R.string.add), color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    //.background(Color.Red)
                    .then(Modifier.padding(8.dp))
            ) {
                Text(stringResource(id = R.string.cancel), color = Color.White)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyWordsTable(myWords: List<MyWords>, polAngViewModel: PolAngViewModel) {
    // Grupowanie słów według pierwszej litery
    val groupedWords = myWords.groupBy { it.word.firstOrNull()?.uppercaseChar() ?: ' ' }

    LazyColumn {
        groupedWords.forEach { (initial, wordsGroup) ->
            if (initial != ' ') {
                stickyHeader {
                    Text(
                        text = initial.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
            }
            itemsIndexed(wordsGroup, key = { _, item -> item.id }) { index, wordItem ->
                MyWordRow(wordItem, index % 2 == 0, polAngViewModel)
                Divider(color = Color.LightGray)
            }
        }
    }
}

@Composable
fun MyWordRow(wordItem: MyWords, isEven: Boolean, polAngViewModel: PolAngViewModel) {
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
        IconButton(onClick = { polAngViewModel.deleteMyWord(wordItem) }) {
            Icon(Icons.Rounded.Delete,
                contentDescription = "Delete",
                tint = Color.DarkGray
                    )
        }
    }
}