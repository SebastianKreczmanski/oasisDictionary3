package com.skreczmanski.oasisdictionary.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

class LogoViewModel : ViewModel() {
    val logoPosition = mutableStateOf(Offset(0f, 0f))
    val logoSize = mutableStateOf(100.dp)

}