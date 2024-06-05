@file:Suppress("DEPRECATION")

package com.skreczmanski.oasisdictionary

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.skreczmanski.oasisdictionary.api.PolAngApiService
import com.skreczmanski.oasisdictionary.dao.PolAngDao
import com.skreczmanski.oasisdictionary.database.PolAngDatabase
import com.skreczmanski.oasisdictionary.network.RetrofitInstance.apiService
import com.skreczmanski.oasisdictionary.screens.main.MainScreen
import com.skreczmanski.oasisdictionary.ui.theme.OasisDictionaryTheme
import com.skreczmanski.oasisdictionary.viewmodel.PolAngViewModel
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            PolAngDatabase::class.java,
            "polang.db"
        )
            .addMigrations(MIGRATION_9_10) // Dodaj migrację, jeśli jest potrzebna
            .fallbackToDestructiveMigration() // Użyj tylko, jeśli utrata danych jest akceptowalna
            .build()
    }

    val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Sprawdź, czy tabela 'favorites' istnieje
                val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='favorites'")
                if (cursor.moveToFirst()) {
                    // Jeśli tabela istnieje, wykonaj operacje migracji
                    db.execSQL("DROP TABLE `favorites`")
                }
                cursor.close()

                // Utwórz nową tabelę 'favorites'
                db.execSQL("CREATE TABLE IF NOT EXISTS `favorites_new` (`id` INTEGER PRIMARY KEY NOT NULL, `word` TEXT NOT NULL, `translation` TEXT NOT NULL)")
                // Przenieś dane ze starej tabeli do nowej, jeśli to konieczne
                // database.execSQL("INSERT INTO `favorites_new` (id, word, translation) SELECT id, word, translation FROM `favorites`")
                db.execSQL("ALTER TABLE `favorites_new` RENAME TO `favorites`")
                db.execSQL("CREATE TABLE IF NOT EXISTS `mywords` (`id` INTEGER PRIMARY KEY NOT NULL, `word` TEXT NOT NULL, `translation` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `allwords` (`id` INTEGER PRIMARY KEY NOT NULL, `word` TEXT NOT NULL, `translation` TEXT NOT NULL)")

            }
    }

    private val viewModel by viewModels<PolAngViewModel> {
        PolAngViewModelFactory(applicationContext, db.dao, apiService)
    }

    private val viewModelFactory by lazy {
        PolAngViewModelFactory(applicationContext, db.dao, apiService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isCellularDataEnabled = sharedPreferences.getBoolean("cellularData", false)
        val isWifiDataEnabled = sharedPreferences.getBoolean("wifiData", true)
        val isEnglishLanguageEnabled = sharedPreferences.getBoolean("englishLanguage", false)
        val isNetworkAvailable = checkNetworkAvailability()

        if (isEnglishLanguageEnabled) {
            setLocale("en")
        } else {
            resetToSystemLanguage()
        }

        if (isNetworkAvailable && (isWifiDataEnabled || isCellularDataEnabled)) {
            viewModel.fetchTranslations()
        } else {
            showNoInternetDialog()
        }

        viewModel.isDatabaseEmpty().observe(this) { isEmpty ->
            Handler(Looper.getMainLooper()).postDelayed({
                if (isEmpty) {
                    showEmptyDatabaseDialog()
                }
            }, 3000) // Opóźnienie 3000 ms, czyli 3 sekundy
        }

        setContent {
            OasisDictionaryTheme {
                MainScreen(viewModelFactory)
            }
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun checkNetworkAvailability(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun resetToSystemLanguage() {
        val locale = Locale.getDefault()
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    // Metoda do wyświetlania dialogu o braku internetu
    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.no_internet_title)) // Tytuł z resources
            .setMessage(getString(R.string.no_internet_message)) // Wiadomość z resources
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Metoda do wyświetlania dialogu o pustej bazie danych
    private fun showEmptyDatabaseDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.empty_database_title)) // Tytuł z resources
            .setMessage(getString(R.string.empty_database_message)) // Wiadomość z resources
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

}

class PolAngViewModelFactory(
    private val context: Context,
    private val dao: PolAngDao,
    private val apiService: PolAngApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PolAngViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PolAngViewModel(context, dao, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}