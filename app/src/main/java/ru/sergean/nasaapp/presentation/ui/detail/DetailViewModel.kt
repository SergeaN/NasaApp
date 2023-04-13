package ru.sergean.nasaapp.presentation.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.domain.AddToFavoritesUseCase
import ru.sergean.nasaapp.domain.RemoveFromFavoritesUseCase
import javax.inject.Inject

class DetailViewModel(
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
) : ViewModel() {

    fun addToFavorites(nasaId: String) {
        viewModelScope.launch {
            try {
                addToFavoritesUseCase(nasaId)
            } catch (e: Exception) {
                Log.e(TAG, "addToFavorites: ", e)
            }
        }
    }

    fun removeFromFavorites(nasaId: String) {
        viewModelScope.launch {
            try {
                removeFromFavoritesUseCase(nasaId)
            } catch (e: Exception) {
                Log.e(TAG, "removeFromFavorites: ", e)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val addToFavoritesUseCase: AddToFavoritesUseCase,
        private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(addToFavoritesUseCase, removeFromFavoritesUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
