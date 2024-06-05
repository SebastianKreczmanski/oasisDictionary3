package com.skreczmanski.oasisdictionary.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skreczmanski.oasisdictionary.api.PolAngApiService
import com.skreczmanski.oasisdictionary.dao.PolAngDao
import com.skreczmanski.oasisdictionary.entities.AllWords
import com.skreczmanski.oasisdictionary.entities.FavoriteWord
import com.skreczmanski.oasisdictionary.entities.MyWords
import com.skreczmanski.oasisdictionary.entities.PolAngEntry
import com.skreczmanski.oasisdictionary.screens.settings.getSetting
import com.skreczmanski.oasisdictionary.z_jsona.PolAngItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PolAngViewModel(val context: Context, private val dao: PolAngDao, private val apiService: PolAngApiService) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _words = MutableStateFlow<List<PolAngEntry>>(emptyList())

    private val _searchQueryEng = MutableStateFlow("")
    private val _wordsEng = MutableStateFlow<List<PolAngEntry>>(emptyList())

    private val _favoriteWords = MediatorLiveData<List<FavoriteWord>>()

    private val _ulubione = MutableStateFlow<List<FavoriteWord>>(emptyList())

    val words: StateFlow<List<PolAngEntry>> = _words.asStateFlow()
    val wordsEng: StateFlow<List<PolAngEntry>> = _wordsEng.asStateFlow()
    val ulubione: StateFlow<List<FavoriteWord>> = _ulubione.asStateFlow()

    private val favoriteWordsUpdate = MutableStateFlow(Unit)

    val myWords: StateFlow<List<MyWords>> = dao.mojeSlowkaSortowane().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _allWords = MutableStateFlow<List<AllWords>>(emptyList())
    val allWords: StateFlow<List<AllWords>> = _allWords.asStateFlow()

    private val _allWordsEng = MutableStateFlow<List<AllWords>>(emptyList())
    val allWordsEng: StateFlow<List<AllWords>> = _allWordsEng.asStateFlow()

    init
    {
        fetchAllWords()
        fetchAllWordsEng()
        setupSearch()
        setupSearchEng()
        fetchAllFavorites()
        fetchWszystkieSlowkaPolskie()
        fetchWszystkieSlowkaPolskieEng()
        if (getSetting("searchMyWords", false, context)) {
            copyWordsToAllWords()
        }
    }

    private fun fetchAllWords() {
        viewModelScope.launch {
            dao.slowkaSortowanePoPolsku().collect { wordsList ->
                _words.value = wordsList
            }
        }
    }

    private fun fetchAllWordsEng() {
        viewModelScope.launch {
            dao.slowkaSortowanePoAngielsku().collect { wordsList ->
                _wordsEng.value = wordsList
            }
        }
    }

    private fun fetchAllFavorites() {
        viewModelScope.launch {
            dao.getAllFavoriteWords2().collect {
                wordsList ->
                _ulubione.value = wordsList
            }
        }
    }

    private fun fetchWszystkieSlowkaPolskie() {
        viewModelScope.launch {
            dao.wszystkieSlowkaWKolejnosciPolskiej().collect {
                wordsList ->
                _allWords.value = wordsList
            }
        }
    }

    private fun fetchWszystkieSlowkaPolskieEng() {
        viewModelScope.launch {
            dao.wszystkieSlowkaWKolejnosciAngielskiej().collect {
                    wordsList ->
                _allWordsEng.value = wordsList
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun setupSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(3000)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isEmpty()) {
                        dao.slowkaSortowanePoPolsku()
                    } else {
                        dao.getWords(query)
                    }
                }
                .collect { result ->
                    _words.value = result.takeIf { it.size > 1 } ?: emptyList()
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun setupSearchEng() {
        viewModelScope.launch {
            _searchQueryEng
                .debounce(3_000)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isEmpty()) {
                        dao.slowkaSortowanePoAngielsku()
                    } else {
                        dao.getWordsEng(query)
                    }
                }
                .collect { result ->
                    _wordsEng.value = result.takeIf { it.size > 1 } ?: emptyList()
                }
        }
    }

    fun copyWordsToAllWords() {
        viewModelScope.launch {
            val polangWords = dao.slowkaSortowanePoPolsku().first().map {
                AllWords(
                    id = it.id,
                    word = it.word,
                    translation = it.translation)
            }
            val myWords = dao.mojeSlowkaSortowane().first().map {
                AllWords(
                    id = it.id,
                    word = it.word,
                    translation = it.translation)
            }
            val allWords = (polangWords + myWords).distinctBy { it.word }

            allWords.forEach { allWord ->
                dao.upsertAllWords(allWord)
            }
        }
    }

    fun searchWords(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (getSetting("searchMyWords", false, context)) {
                if (query.isEmpty()) {
                    _allWords.value = dao.wszystkieSlowkaWKolejnosciPolskiej().first()
                } else {
                    _allWords.value = dao.getWszystkieSlowkaPol(query).first()
                }
            } else {
                if (query.isEmpty()) {
                    _words.value = dao.slowkaSortowanePoPolsku().first()
                } else {
                    _words.value = dao.getWords(query).first()
                }
            }
        }
    }

    fun searchWordsEng(query: String) {
        val modifiedQuery = query.trim().removePrefix("a ").removePrefix("the ")
        _searchQueryEng.value = modifiedQuery
        viewModelScope.launch {
            if (getSetting("searchMyWords", false, context)) {
                if (query.isEmpty()) {
                    _allWordsEng.value = dao.wszystkieSlowkaWKolejnosciAngielskiej().first()
                } else {
                    _allWordsEng.value = dao.getWszystkiSlowkaEng(query).first()
                }
            } else {
                if (query.isEmpty()) {
                    _wordsEng.value = dao.slowkaSortowanePoAngielsku().first()
                } else {
                    _wordsEng.value = dao.getWordsEng(query).first()
                }
            }
        }
    }

    fun fetchTranslations() {
        viewModelScope.launch {
            try {
                val translations = apiService.getTranslations()
                translations.forEach { item ->
                    upsertPolAngItem(item)
                }
            } catch (e: Exception) {
                Log.e("PolAngViewModel", "Error fetching translations", e)
            }
        }
    }

    private fun upsertPolAngItem(item: PolAngItem) {
        viewModelScope.launch {
            dao.upsertSlowko(PolAngEntry(item.id, item.word, item.translation))
        }
    }

    fun resetSearchQuery() {
        _searchQuery.value = ""
    }

    fun resetSearchQueryEng() {
        _searchQueryEng.value = ""
    }

    fun addToFavorites(word: PolAngEntry) {
        viewModelScope.launch {
            if (!isFavorite(word)) {
                val favoriteWord = FavoriteWord(id = word.id, word = word.word, translation = word.translation)
                dao.upsertFavoriteWord(favoriteWord)
                favoriteWordsUpdate.value = Unit // Aktualizacja
            }
        }
    }

    fun deleteFromFavorites(word: PolAngEntry) {
        viewModelScope.launch {
            val favoriteWord = FavoriteWord(id = word.id, word = word.word, translation = word.translation)
            dao.deleteFavoriteWord(favoriteWord)
            favoriteWordsUpdate.value = Unit // Aktualizacja
        }
    }

    private fun isFavorite(word: PolAngEntry): Boolean {
        return _favoriteWords.value?.any { it.word == word.word && it.translation == word.translation } ?: false
    }

    fun addMyWord(word: String, translation: String) {
        viewModelScope.launch {
            val currentMaxId = dao.getMaxIdMyWords() ?: 899999
            val newId = currentMaxId + 1
            val newWord = MyWords(id = newId, word = word, translation = translation)
            dao.upsertMyWords(newWord)
            if (getSetting("searchMyWords", false, context)) {
                dao.upsertAllWords(AllWords(id = newId, word = word, translation = translation))
            }
        }
    }

    // Nowa funkcja do usuwania słowa
    fun deleteMyWord(wordItem: MyWords) {
        viewModelScope.launch {
            // Usuwanie słowa z tabeli MyWords
            dao.deleteMyWords(wordItem)

            // Sprawdzanie, czy słowo jest w ulubionych i usuwanie go stamtąd
            val favoriteWord = dao.getFavoriteWordById(wordItem.id)
            if (favoriteWord != null) {
                dao.deleteFavoriteWord(favoriteWord)
            }

            // Sprawdzanie, czy słowo jest w tabeli AllWords i usuwanie go stamtąd
            val allWord = dao.getAllWordById(wordItem.id)
            if (allWord != null) {
                dao.deleteAllWords(allWord)
            }

            // Aktualizacja strumieni danych
            fetchWszystkieSlowkaPolskie()
            favoriteWordsUpdate.value = Unit
        }
    }

    //sprawdzenie czy baza nie jest przez przypadek pusta
    fun isDatabaseEmpty(): LiveData<Boolean> {
        val isEmptyLiveData = MediatorLiveData<Boolean>()
        val countLiveData = dao.getCount()

        isEmptyLiveData.addSource(countLiveData) { count ->
            isEmptyLiveData.value = (count == 0)
        }
        return isEmptyLiveData
    }
}