package ru.sergean.nasaapp.presentation.ui.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.domain.images.AddToFavoritesUseCase
import ru.sergean.nasaapp.domain.images.FetchFavoriteImagesUseCase
import ru.sergean.nasaapp.domain.images.RemoveFromFavoritesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.favorites.items.mapFavoriteImageItem
import javax.inject.Inject

class FavoritesViewModel(
    private val fetchImagesUseCase: FetchFavoriteImagesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
) : BaseViewModel<FavoritesState, FavoritesAction, FavoritesEffect>(
    initialState = FavoritesState()
) {

    override fun dispatch(action: FavoritesAction) {
        when (action) {
            is FavoritesAction.Refresh -> reduce(action)
            is FavoritesAction.ChangeQuery -> reduce(action)
            is FavoritesAction.RemoveFromFavorites -> reduce(action)
        }
    }

    private fun reduce(action: FavoritesAction.Refresh) {
        if (viewState.progress) {
            sideEffect = FavoritesEffect.Message(text = "In process!")
        } else {
            fetchImages(query = viewState.query)
        }
    }

    private fun reduce(action: FavoritesAction.ChangeQuery) {
        if (viewState.progress) {
            sideEffect = FavoritesEffect.Message(text = "In process!")
        } else {
            viewState = viewState.copy(query = action.query)
        }
    }

    private fun fetchImages(query: String) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            viewState = try {
                val images = fetchImagesUseCase.invoke(query).map { it.mapFavoriteImageItem() }
                Log.d(TAG, "fetchImages: $viewState")

                if (viewState.images != images) viewState.copy(progress = false, images = images)
                else viewState.copy(progress = false)
            } catch (e: Exception) {
                Log.e(TAG, "fetchFavorites:", e)
                viewState.copy(progress = false)
            }
        }
    }

    private fun reduce(action: FavoritesAction.RemoveFromFavorites) {
        withViewModelScope {
            try {
                removeFromFavoritesUseCase(action.nasaId)
                val newImages = viewState.images.filter { it.nasaId != action.nasaId }
                if (newImages.size != viewState.images.size) {
                    viewState = viewState.copy(images = newImages)
                }
            } catch (e: Exception) {
                Log.e(TAG, "removeFromFavorites: ", e)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val fetchImagesUseCase: FetchFavoriteImagesUseCase,
        private val addToFavoritesUseCase: AddToFavoritesUseCase,
        private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                return FavoritesViewModel(
                    fetchImagesUseCase, addToFavoritesUseCase, removeFromFavoritesUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}