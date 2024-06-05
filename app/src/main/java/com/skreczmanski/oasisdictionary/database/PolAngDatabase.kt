package com.skreczmanski.oasisdictionary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skreczmanski.oasisdictionary.dao.PolAngDao
import com.skreczmanski.oasisdictionary.entities.AllWords
import com.skreczmanski.oasisdictionary.entities.FavoriteWord
import com.skreczmanski.oasisdictionary.entities.MyWords
import com.skreczmanski.oasisdictionary.entities.PolAngEntry

@Database(
    entities = [PolAngEntry::class,
               FavoriteWord::class,
               MyWords::class,
               AllWords::class],
    version = 10,
    exportSchema = false
)

abstract class PolAngDatabase: RoomDatabase() {

    abstract val dao: PolAngDao
}