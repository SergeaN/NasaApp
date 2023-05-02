package ru.sergean.nasaapp.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.di.app.AppScope
import ru.sergean.nasaapp.di.app.ApplicationContext
import javax.inject.Inject

@AppScope
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
        try {
            dataStore.edit { it[introKey] = true }
        } catch (e: Exception) {
            Log.e(TAG, "introShowed:", e)
        }
    }

    suspend fun login() {
        try {
            dataStore.edit { it[loggedKey] = true }
        } catch (e: Exception) {
            Log.e(TAG, "login:", e)
        }
    }

    suspend fun logout() {
        try {
            dataStore.edit { it[loggedKey] = false }
        } catch (e: Exception) {
            Log.e(TAG, "logout:", e)
        }
    }
}