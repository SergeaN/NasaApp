package ru.sergean.nasaapp.presentation.ui.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.domain.AddToFavoritesUseCase
import ru.sergean.nasaapp.domain.FetchFavoriteImagesUseCase
import ru.sergean.nasaapp.domain.RemoveFromFavoritesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State
import ru.sergean.nasaapp.presentation.ui.home.HomeAction
import ru.sergean.nasaapp.presentation.ui.home.HomeEffect
import ru.sergean.nasaapp.presentation.ui.home.mapToImageItem
import javax.inject.Inject

data class FavoritesState(
    val progress: Boolean = false,
    val query: String = "",
    val images: List<FavoriteImageItem> = emptyList(),
) : State

sealed interface FavoritesAction : Action {
    data class Refresh(val force: Boolean = false) : FavoritesAction
    data class ChangeQuery(val query: String) : FavoritesAction
    data class RemoveFromFavorites(val nasaId: String): FavoritesAction
}

sealed interface FavoritesEffect : Effect {
    data class Message(val text: String) : FavoritesEffect
}

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
        when {
            viewState.progress -> {
                sideEffect = FavoritesEffect.Message(text = "In process!")
            }
            action.force -> {
                fetchImages(query = viewState.query)
            }
            viewState.images.isEmpty() && viewState.query.isEmpty() -> {
                fetchImages(query = viewState.query)
            }
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
                val images = fetchImagesUseCase.invoke(query)
                viewState.copy(progress = false, images = images.map { it.mapFavoriteImageItem() })
            } catch (e: Exception) {
                Log.e(TAG, "fetchFavorites:", e)
                viewState.copy(progress = false)
            }
        }
    }
/*
    private fun addToFavorites(nasaId: Int) {
        withViewModelScope {
            try {
                addToFavoritesUseCase(nasaId)
            } catch (e: Exception) {
                Log.e(TAG, "addToFavorites: ", e)
            }
        }
    }*/

    private fun reduce(action: FavoritesAction.RemoveFromFavorites) {
        withViewModelScope {
            try {
                removeFromFavoritesUseCase(action.nasaId)
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