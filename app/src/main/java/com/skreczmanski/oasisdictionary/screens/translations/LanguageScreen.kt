package com.skreczmanski.oasisdictionary.screens.translations

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skreczmanski.oasisdictionary.R
import com.skreczmanski.oasisdictionary.viewmodel.LogoViewModel

@Composable
fun LanguageScreen(navController: NavHostController, logoViewModel: LogoViewModel) {
    val logoPositionPx = logoViewModel.logoPosition.value
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tlo_oaza),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val logoPositionDp = with(density) { logoPositionPx.y.toDp() } + 48.dp
            Spacer(modifier = Modifier.height(logoPositionDp)) // Dynamiczna odległość zależna od pozycji logo

            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(36.dp)) // Zwiększona przestrzeń między tekstem a kartą

            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .shadow(8.dp)
                    .clip(RoundedCornerShape(16.dp)), // Zaokrąglenie rogów karty
                color = Color.White.copy(alpha = 0.9f) // Białe tło z przezroczystością
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "English",
                        fontSize = 20.sp,
                        color = Color.Black // Ciemniejsza czcionka
                    )

                    Spacer(modifier = Modifier.height(24.dp)) // Zwiększona przestrzeń między tekstem a przyciskami

                    LanguageButtonRow(
                        flagStart = R.drawable.flag_of_poland,
                        flagEnd = R.drawable.flag_of_the_united_kingdom,
                        onClick = { navController.navigate("polEngDictionary") }
                    )

                    Divider() // Linia oddzielająca przyciski

                    Spacer(modifier = Modifier.height(8.dp))

                    LanguageButtonRow(
                        flagStart = R.drawable.flag_of_the_united_kingdom,
                        flagEnd = R.drawable.flag_of_poland,
                        onClick = { navController.navigate("engPolDictionary") }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageButtonRow(flagStart: Int, flagEnd: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = flagStart),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Image(
                painter = painterResource(id = flagEnd),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}
