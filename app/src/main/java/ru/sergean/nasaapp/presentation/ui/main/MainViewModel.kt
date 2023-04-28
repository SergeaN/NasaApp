package ru.sergean.nasaapp.presentation.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import javax.inject.Inject

class MainViewModel @Inject constructor(dataStore: SettingDataStore) : ViewModel() {

    val mode: SharedFlow<StartMode> =
        dataStore.startMode.shareIn(viewModelScope, started = SharingStarted.Eagerly, replay = 1)

    private val SettingDataStore.startMode: Flow<StartMode>
        get() = settingsBundle.map { settings ->
            Log.d(TAG, "MainViewModel: $settings")
            when {
                settings.isUserLogged -> StartMode.ShowApp
                settings.isIntroShowed -> StartMode.SkipIntro
                else -> StartMode.ShowIntro
            }
        }
}