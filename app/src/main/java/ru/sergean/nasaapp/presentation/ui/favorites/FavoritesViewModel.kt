package ru.sergean.nasaapp.presentation.ui.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.domain.FetchFavoriteImagesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State
import ru.sergean.nasaapp.presentation.ui.home.mapToImageItem
import javax.inject.Inject

data class FavoritesState(
    val progress: Boolean = false,
    val query: String = "",
    val images: List<FavoriteImageItem> = emptyList(),
) : State

sealed interface FavoritesAction : Action {
    object Refresh : FavoritesAction
    data class ChangeQuery(val query: String) : FavoritesAction
}

sealed interface FavoritesEffect : Effect {
    data class Message(val text: String) : FavoritesEffect
}

class FavoritesViewModel(
    private val fetchImagesUseCase: FetchFavoriteImagesUseCase
) : BaseViewModel<FavoritesState, FavoritesAction, FavoritesEffect>(
    initialState = FavoritesState()
) {

    override fun dispatch(action: FavoritesAction) {
        when (action) {
            is FavoritesAction.Refresh -> reduce(action)
            is FavoritesAction.ChangeQuery -> reduce(action)
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

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val fetchImagesUseCase: FetchFavoriteImagesUseCase
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                return FavoritesViewModel(fetchImagesUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}