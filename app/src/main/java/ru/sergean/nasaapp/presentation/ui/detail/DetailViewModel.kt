package ru.sergean.nasaapp.presentation.ui.detail

import kotlinx.coroutines.CoroutineScope
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.domain.images.AddToFavoritesUseCase
import ru.sergean.nasaapp.domain.images.GetImageUseCase
import ru.sergean.nasaapp.domain.images.RemoveFromFavoritesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel

class DetailViewModel(
    private val nasaId: String,
    private val getImageUseCase: GetImageUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
) : BaseViewModel<DetailState, DetailAction, DetailEffect>(initialState = DetailState()) {

    override fun dispatch(action: DetailAction) {
        when (action) {
            is DetailAction.GetImage -> reduce(action)
            is DetailAction.ChangeSaving -> reduce(action)
        }
    }

    private fun reduce(action: DetailAction.GetImage) {
        if (viewState.progress) {
            sideEffect = DetailEffect.Message(text = R.string.in_process)
        } else {
            getImage(nasaId)
        }
    }

    private fun reduce(action: DetailAction.ChangeSaving) {
        if (viewState.imageSaved) {
            changeSaving { removeFromFavoritesUseCase(nasaId) }
        } else {
            changeSaving { addToFavoritesUseCase(nasaId) }
        }
    }

    private fun getImage(nasaId: String) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            viewState = when (val result = getImageUseCase(nasaId)) {
                is ResultWrapper.Success -> {
                    viewState.copy(
                        progress = false, image = result.data, imageSaved = result.data.isFavorite
                    )
                }
                is ResultWrapper.Failure -> {
                    viewState.copy(progress = false, imageSaved = false)
                }
            }
        }
    }
    
    private fun changeSaving(changeAction: suspend CoroutineScope.() -> Unit) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            changeAction()
            viewState = viewState.copy(progress = false, imageSaved = !viewState.imageSaved)
        }
    }
}