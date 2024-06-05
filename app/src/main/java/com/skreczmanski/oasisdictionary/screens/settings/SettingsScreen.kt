@file:Suppress("DEPRECATION")

package com.skreczmanski.oasisdictionary.screens.settings

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skreczmanski.oasisdictionary.R
import com.skreczmanski.oasisdictionary.viewmodel.LogoViewModel
import com.skreczmanski.oasisdictionary.viewmodel.PolAngViewModel
import java.util.Locale

fun saveSetting(key: String, value: Boolean, context: Context) {
    val sharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        putBoolean(key, value)
        apply()
    }
    Log.d("Settings", "Saved $key: $value")
}

fun getSetting(key: String, defaultValue: Boolean, context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    val value = sharedPref.getBoolean(key, defaultValue)
    Log.d("Settings", "Got $key: $value")
    return value
}

@Composable
fun SettingsScreen(
    navController: NavHostController,
    logoViewModel: LogoViewModel,
    polAngViewModel: PolAngViewModel
) {

    val logoPosition = logoViewModel.logoPosition.value
    val logoSize = logoViewModel.logoSize.value
    val tloPaskaNawigacji = painterResource(id = R.drawable.pasek_gora_sahara2)
    val jasneTlo = Color(0xFFFDF5E6) // Bardzo jasny odcień kremowego, prawie biały

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current

    val endPadding = with(density) {
        screenWidth - logoPosition.x.toDp() - logoSize + 40.dp
    }

    val context = LocalContext.current

    // Początkowy stan przełączników
    val cellularDataEnabled =
        remember { mutableStateOf(getSetting("cellularData", false, context)) }
    val wifiDataEnabled = remember { mutableStateOf(getSetting("wifiData", true, context)) }
    val englishLanguageEnabled =
        remember { mutableStateOf(getSetting("englishLanguage", false, context)) }

    val languageChanged = remember { mutableStateOf(false) }

    val searchMyWordsEnabled =
        remember { mutableStateOf(getSetting("searchMyWords", false, context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(jasneTlo)
    ) {
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

                // Drugi rząd z napisem "Ustawienia"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = endPadding), // Używamy obliczonego odstępu
                    horizontalArrangement = Arrangement.End // Wyrównanie do prawej
                ) {
                    Text(
                        text = stringResource(id = R.string.settings), // Zmieniony tekst na "Ustawienia"
                        fontSize = 24.sp, // Rozmiar czcionki
                        fontWeight = FontWeight.Bold, // Pogrubiona czcionka
                        modifier = Modifier.alignByBaseline() // Wyrównanie do linii bazowej
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column {
                SwitchItem(
                    label = stringResource(id = R.string.dane_komorkowe),
                    isChecked = cellularDataEnabled.value,
                    onCheckedChange = { newValue ->
                        cellularDataEnabled.value = newValue
                        saveSetting("cellularData", newValue, context)
                        if (newValue || wifiDataEnabled.value) {
                            polAngViewModel.fetchTranslations() // Pobieranie danych
                        }
                    }
                )

                Divider()

                SwitchItem(
                    label = stringResource(id = R.string.dane_wifi),
                    isChecked = wifiDataEnabled.value,
                    onCheckedChange = { newValue ->
                        wifiDataEnabled.value = newValue
                        saveSetting("wifiData", newValue, context)
                        if (newValue || cellularDataEnabled.value) {
                            polAngViewModel.fetchTranslations() // Pobieranie danych
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nowa karta dla przełącznika języka angielskiego
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            SwitchItem(
                label = stringResource(id = R.string.use_englsh),
                isChecked = englishLanguageEnabled.value,
                onCheckedChange = { newValue ->
                    englishLanguageEnabled.value = newValue
                    saveSetting("englishLanguage", newValue, context)
                    updateLanguageSettings(newValue, context)
                    languageChanged.value = !languageChanged.value // Zmiana stanu
                }
            )
        }
        if (languageChanged.value) {
            // Renderuj komponenty, które powinny zareagować na zmianę języka
        }

        //Karta do przeszukiwania słownika z dodanymi wyrazami
        Spacer(modifier = Modifier.height(16.dp))

        // Nowa karta dla przełącznika wyszukiwania w dodanych słowach
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            SwitchItem(
                label = stringResource(id = R.string.search_my_words), // Tekst etykiety
                isChecked = searchMyWordsEnabled.value,
                onCheckedChange = { newValue ->
                    searchMyWordsEnabled.value = newValue
                    saveSetting("searchMyWords", newValue, context)
                    if (newValue) {
                        polAngViewModel.copyWordsToAllWords()

                    }
                }
            )
        }
    }
}

@Composable
fun SwitchItem(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = Color.Black
        )
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

private fun updateLanguageSettings(useEnglish: Boolean, context: Context) {
    if (useEnglish) {
        setLocale("en", context)
    } else {
        resetToSystemLanguage(context)
    }
}

fun setLocale(languageCode: String, context: Context) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

fun resetToSystemLanguage(context: Context) {
    val systemLocale = Resources.getSystem().configuration.locales.get(0)
    Locale.setDefault(systemLocale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(systemLocale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}