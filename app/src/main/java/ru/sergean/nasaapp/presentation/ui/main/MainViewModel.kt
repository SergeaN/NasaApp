package ru.sergean.nasaapp.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import javax.inject.Inject

class MainViewModel(private val dataStore: SettingDataStore) : ViewModel() {

    private val _mode: MutableStateFlow<StartMode?> = MutableStateFlow(value = null)
    val mode: StateFlow<StartMode?>
        get() = _mode

    init {
        viewModelScope.launch {
            _mode.value = StartMode.ShowApp
/*
            dataStore.settingsBundle.collect { settings ->
                when {
                    settings.isUserLogged -> _mode.value = StartMode.ShowApp
                    settings.isIntroShowed -> _mode.value = StartMode.SkipIntro
                    else -> _mode.value = StartMode.ShowIntro
                }
                cancel()
            }
*/
        }
    }

    class Factory @Inject constructor(
        private val dataStore: SettingDataStore
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}