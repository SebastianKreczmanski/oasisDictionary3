package com.skreczmanski.oasisdictionary.screens.translations.PolEng

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skreczmanski.oasisdictionary.R
import com.skreczmanski.oasisdictionary.entities.AllWords
import com.skreczmanski.oasisdictionary.entities.PolAngEntry
import com.skreczmanski.oasisdictionary.screens.settings.getSetting
import com.skreczmanski.oasisdictionary.viewmodel.PolAngViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EngPolDictionary(navController: NavHostController, polAngViewModel: PolAngViewModel) {
    var searchText by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        polAngViewModel.resetSearchQueryEng()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderImageEng()
        LanguageSwitchCardEng(
            navController,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 150.dp)
                .background(Color.White)
        )
        Column(
            modifier = Modifier
                .padding(top = 230.dp)
                .background(Color.White)
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                query = searchText,
                onQueryChange = {
                    searchText = it
                    polAngViewModel.searchWordsEng(it)
                },
                onSearch = {
                    active = false
                },
                active = active,
                onActiveChange = {
                    active = it
                    if (it) {
                        keyboardController?.show()
                    } else {
                        keyboardController?.hide()
                    }
                },
                placeholder = {
                    androidx.compose.material.Text(stringResource(R.string.wyszukaj))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        tint = Color.Black,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (active) {
                        Icon(
                            modifier = Modifier
                                .clickable {
                                    if (searchText.isNotEmpty()) {
                                        searchText = ""
                                    } else {
                                        active = false
                                        polAngViewModel.resetSearchQueryEng()
                                    }
                                },
                            imageVector = Icons.Default.Close,
                            tint = Color.Black,
                            contentDescription = "Close Icon"
                        )
                    }
                },
                colors = SearchBarDefaults.colors(
                    containerColor = Color.LightGray
                )
            ) {
                WordsTableEng(polAngViewModel)
            }
            WordsTableEng(polAngViewModel)
        }
    }
}

@Composable
fun HeaderImageEng() {
    Image(
        painter = painterResource(id = R.drawable.krakow),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.Transparent),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LanguageSwitchCardEng(navController: NavHostController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(250.dp)
            .height(70.dp)
            .background(Color.White) // Upewnij się, że tło karty jest białe
            .clickable { navController.navigate("PolEngDictionary") }
            .shadow(6.dp, shape = RoundedCornerShape(8.dp)), // Cień dla karty
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .background(color = Color.Transparent), // Usunięcie tła i obramowania z rzędu
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.flag_of_the_united_kingdom),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.flag_of_poland),
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordsTableEng(viewModel: PolAngViewModel) {
    val useMyWords = getSetting("searchMyWords", false, viewModel.context)
    val words by if (useMyWords) viewModel.allWordsEng.collectAsState() else viewModel.wordsEng.collectAsState()
    val groupedWords = words.groupBy { wordItem ->
        val word = when (wordItem) {
            is PolAngEntry -> wordItem.translation
            is AllWords -> wordItem.translation
            else -> ""
        }
        val modifiedWord = removeContentInBrackets(word).removePrefix("a ").removePrefix("the ")
        modifiedWord.firstOrNull()?.uppercaseChar() ?: ' '
    }.toSortedMap()

    LazyColumn(modifier = Modifier.padding(bottom = 5.dp)) {
        ('A'..'Z').forEach { initial ->
            groupedWords[initial]?.let { wordsGroup ->
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

                itemsIndexed(wordsGroup, key = { _, item ->
                    when (item) {
                        is PolAngEntry -> item.id
                        is AllWords -> item.id
                        else -> 0 // lub inna domyślna wartość
                    }
                }) { index, wordItem ->
                    when (wordItem) {
                        is PolAngEntry -> WordRowEng(
                            wordItem,
                            index % 2 == 0,
                            viewModel
                        )

                        is AllWords -> WordRowEng(
                            PolAngEntry(
                                wordItem.id,
                                wordItem.word,
                                wordItem.translation
                            ), // Przekształć AllWords na PolAngEntry
                            index % 2 == 0, viewModel
                        )
                    }
                    Divider(color = Color.LightGray)
                }
            }
        }
    }
}

fun removeContentInBrackets(word: String): String {
    return word.replace(Regex("\\(.*?\\)"), "").trim()
}

@Composable
fun WordRowEng(
    wordItem: PolAngEntry,
    isEven: Boolean,
    viewModel: PolAngViewModel
) {
    val favoriteWords by viewModel.ulubione.collectAsState(initial = emptyList())
    val isFavorite = favoriteWords.any { it.translation == wordItem.translation }
    val backgroundColor = if (isEven) Color(0xFFE0E0E0) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = wordItem.translation,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = wordItem.word,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
        IconButton(onClick = {
            if (isFavorite) {
                viewModel.deleteFromFavorites(wordItem)
            } else {
                viewModel.addToFavorites(wordItem)
            }
        }) {
            val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
            val tint = if (isFavorite) Color.Red else Color.Black
            Icon(icon, contentDescription = "Add to Favorites", tint = tint)
        }
    }
}