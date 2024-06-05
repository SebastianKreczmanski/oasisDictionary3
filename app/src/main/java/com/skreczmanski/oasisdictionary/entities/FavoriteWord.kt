package com.skreczmanski.oasisdictionary.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteWord(
    @PrimaryKey
    val id: Int,
    val word: String,
    val translation: String
)