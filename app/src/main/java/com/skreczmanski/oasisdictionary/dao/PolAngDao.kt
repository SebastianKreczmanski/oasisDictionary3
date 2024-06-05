package com.skreczmanski.oasisdictionary.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.skreczmanski.oasisdictionary.entities.AllWords
import com.skreczmanski.oasisdictionary.entities.FavoriteWord
import com.skreczmanski.oasisdictionary.entities.MyWords
import com.skreczmanski.oasisdictionary.entities.PolAngEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface PolAngDao {
    @Upsert
    suspend fun upsertSlowko(polAngEntry: PolAngEntry)

    @Delete
    suspend fun deleteSlowko(polAngEntry: PolAngEntry)

    @Query("SELECT id, word, translation FROM polangdict ORDER BY word ASC")
    fun slowkaSortowanePoPolsku(): Flow<List<PolAngEntry>>

    @Query("SELECT id, word, translation FROM polangdict ORDER BY CASE WHEN translation LIKE '(%)%' THEN SUBSTR(translation, INSTR(translation, ')') + 1) WHEN translation LIKE 'a %' THEN SUBSTR(translation, 3) WHEN translation LIKE 'the %' THEN SUBSTR(translation, 5) ELSE translation END ASC")
    fun slowkaSortowanePoAngielsku(): Flow<List<PolAngEntry>>

    @Query("SELECT * FROM polangdict WHERE word LIKE '%' || :query || '%'")
    fun getWords(query: String): Flow<List<PolAngEntry>>

    @Query("SELECT * FROM polangdict WHERE translation LIKE '%' || :query || '%'")
    fun getWordsEng(query: String): Flow<List<PolAngEntry>>

    //Wszystkie słówka łącznie z własnymi
    @Upsert
    suspend fun upsertAllWords(allWords: AllWords)

    @Delete
    suspend fun deleteAllWords(allWords: AllWords)

    @Query("SELECT * FROM allwords ORDER BY word ASC")
    fun wszystkieSlowkaWKolejnosciPolskiej(): Flow<List<AllWords>>

    @Query("SELECT * FROM allwords ORDER BY translation ASC")
    fun wszystkieSlowkaWKolejnosciAngielskiej(): Flow<List<AllWords>>

    @Query("SELECT *  FROM allwords WHERE word LIKE '%' || :query || '%'")
    fun getWszystkieSlowkaPol(query: String): Flow<List<AllWords>>

    @Query("SELECT * FROM allwords WHERE translation LIKE '%' || :query || '%'")
    fun getWszystkiSlowkaEng(query: String): Flow<List<AllWords>>

    @Query("SELECT * FROM allwords WHERE id = :id")
    suspend fun getAllWordById(id: Int): AllWords?

    //Favorites
    @Upsert
    suspend fun upsertFavoriteWord(favoriteWord: FavoriteWord)

    @Delete
    suspend fun deleteFavoriteWord(favoriteWord: FavoriteWord)

    @Query("SELECT * FROM favorites")
    fun getAllFavoriteWords(): LiveData<List<FavoriteWord>>

    @Query("SELECT * FROM favorites")
    fun getAllFavoriteWords2(): Flow<List<FavoriteWord>>

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getFavoriteWordById(id: Int): FavoriteWord?

    //MyWords
    @Upsert
    suspend fun upsertMyWords(myWords: MyWords)

    @Delete
    suspend fun deleteMyWords(myWords: MyWords)

    @Query("SELECT * FROM mywords ORDER BY word ASC")
    fun mojeSlowkaSortowane(): Flow<List<MyWords>>

    @Query("SELECT * FROM mywords WHERE word LIKE '%' || :query || '%'")
    fun getMojeSlowka(query: String): Flow<List<MyWords>>

    @Query("SELECT MAX(id) FROM mywords")
    suspend fun getMaxIdMyWords(): Int?

    @Query("SELECT COUNT(*) FROM polangdict")
    fun getCount(): LiveData<Int>

}