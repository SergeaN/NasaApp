package ru.sergean.nasaapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sergean.nasaapp.di.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingDataStore @Inject constructor(@ApplicationContext context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val dataStore = context.dataStore

    private val introKey = booleanPreferencesKey(name = "intro_showed")
    private val loggedKey = booleanPreferencesKey(name = "user_logged")

    val settingsBundle: Flow<SettingBundle> = dataStore.data.map {
        val isUserLogged = it[loggedKey] ?: false
        val isIntroShowed = it[introKey] ?: false
        SettingBundle(isIntroShowed, isUserLogged)
    }

    suspend fun introShowed() {
        dataStore.edit { it[introKey] = true }
    }

    suspend fun login() {
        dataStore.edit { it[loggedKey] = true }
    }

    suspend fun logout() {
        dataStore.edit { it[loggedKey] = false }
    }
}