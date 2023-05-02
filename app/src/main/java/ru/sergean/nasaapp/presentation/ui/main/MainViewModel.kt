package ru.sergean.nasaapp.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import javax.inject.Inject

class MainViewModel @Inject constructor(dataStore: SettingDataStore) : ViewModel() {

    val mode: SharedFlow<StartMode> =
        dataStore.startMode.shareIn(viewModelScope, started = SharingStarted.Eagerly, replay = 1)

    private val SettingDataStore.startMode: Flow<StartMode>
        get() = settingsBundle.map { settings ->
            when {
                settings.isUserLogged -> StartMode.ShowApp
                settings.isIntroShowed -> StartMode.SkipIntro
                else -> StartMode.ShowIntro
            }
        }
}