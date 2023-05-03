package ru.sergean.nasaapp.presentation.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.sergean.nasaapp.domain.images.AddToFavoritesUseCase
import ru.sergean.nasaapp.domain.images.GetImageUseCase
import ru.sergean.nasaapp.domain.images.RemoveFromFavoritesUseCase

class DetailViewModelFactory @AssistedInject constructor(
    @Assisted("nasaId") private val nasaId: String,
    private val getImageUseCase: GetImageUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(
                nasaId, getImageUseCase, addToFavoritesUseCase, removeFromFavoritesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("nasaId") nasaId: String): DetailViewModelFactory
    }
}
