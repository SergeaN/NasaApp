package ru.sergean.nasaapp.presentation.ui.favorites

import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.domain.images.AddToFavoritesUseCase
import ru.sergean.nasaapp.domain.images.FetchFavoriteImagesUseCase
import ru.sergean.nasaapp.domain.images.RemoveFromFavoritesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.favorites.items.mapFavoriteImageItem
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
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
            is FavoritesAction.AddToFavorites -> reduce(action)
            is FavoritesAction.RemoveFromFavorites -> reduce(action)
        }
    }

    private fun reduce(action: FavoritesAction.Refresh) {
        if (viewState.progress) {
            sideEffect = FavoritesEffect.Message(text = R.string.in_process)
        } else {
            fetchImages(query = viewState.query)
        }
    }

    private fun reduce(action: FavoritesAction.ChangeQuery) {
        if (viewState.progress) {
            sideEffect = FavoritesEffect.Message(text = R.string.in_process)
        } else {
            viewState = viewState.copy(query = action.query)
        }
    }

    private fun reduce(action: FavoritesAction.AddToFavorites) {
        withViewModelScope {
            addToFavoritesUseCase(action.nasaId)
        }
    }

    private fun reduce(action: FavoritesAction.RemoveFromFavorites) {
        withViewModelScope {
            removeFromFavoritesUseCase(action.nasaId)
        }
    }

    private fun fetchImages(query: String) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            viewState = when (val result = fetchImagesUseCase(query)) {
                is ResultWrapper.Success -> {
                    val images = result.data.map { it.mapFavoriteImageItem() }

                    if (viewState.images != images) {
                        viewState.copy(progress = false, images = images)
                    } else {
                        viewState.copy(progress = false)
                    }
                }
                is ResultWrapper.Failure -> {
                    viewState.copy(progress = false)
                }
            }
        }
    }
}