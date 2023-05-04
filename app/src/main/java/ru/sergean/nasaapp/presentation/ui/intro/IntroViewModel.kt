package ru.sergean.nasaapp.presentation.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import javax.inject.Inject

class IntroViewModel @Inject constructor(private val dataStore: SettingDataStore) : ViewModel() {

    val data = listOf(
        IntroItemData(imageId = R.drawable.sun, textId = R.string.sun_text),
        IntroItemData(imageId = R.drawable.saturn, textId = R.string.saturn_text),
        IntroItemData(imageId = R.drawable.planet_earth, textId = R.string.earth_text),
    )

    private val _currentPosition = MutableStateFlow(value = 0)

    val currentPosition: StateFlow<Int>
        get() = _currentPosition

    fun updatePosition(newPosition: Int) {
        _currentPosition.value = newPosition
    }

    fun introShowed() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.introShowed()
        }
    }
}

