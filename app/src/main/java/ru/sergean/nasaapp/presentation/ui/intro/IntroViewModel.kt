package ru.sergean.nasaapp.presentation.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import javax.inject.Inject

class IntroViewModel(private val dataStore: SettingDataStore) : ViewModel() {

    val data = listOf(
        IntroItemData(imageId = R.drawable.sun, textId = R.string.sun_text),
        IntroItemData(imageId = R.drawable.saturn, textId = R.string.saturn_text),
        IntroItemData(imageId = R.drawable.planet_earth, textId = R.string.earth_text),
    )

    var currentPosition: Int = 0

    fun introShowed() {
        viewModelScope.launch {
            dataStore.introShowed()
        }
    }

    class Factory @Inject constructor(
        private val dataStore: SettingDataStore
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(IntroViewModel::class.java)) {
                return IntroViewModel(dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

