package ru.sergean.nasaapp.presentation.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
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

    init {
        getImage(nasaId)
    }

    override fun dispatch(action: DetailAction) {
        when (action) {
            is DetailAction.ChangeSaving -> reduce(action)
        }
    }

    private fun getImage(nasaId: String) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            viewState = try {
                val image = getImageUseCase.invoke(nasaId)
                viewState.copy(
                    progress = false, image = image,
                    imageSaved = image?.isFavorite ?: false
                )
            } catch (e: Exception) {
                sideEffect = DetailEffect.Message(text = "Unknown Error")
                viewState.copy(progress = false, imageSaved = false)
            }
        }
    }

    private fun reduce(action: DetailAction.ChangeSaving) {
        changeSaving {
            if (viewState.imageSaved) {
                removeFromFavoritesUseCase.invoke(nasaId)
            } else {
                addToFavoritesUseCase.invoke(nasaId)
            }
        }
    }

    private fun changeSaving(changeAction: suspend CoroutineScope.() -> Unit) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            try {
                changeAction()
                viewState = viewState.copy(progress = false, imageSaved = !viewState.imageSaved)
            } catch (e: Exception) {
                viewState = viewState.copy(progress = false)
                sideEffect = DetailEffect.Message(text = "Saving Error")
            }
        }
    }
}

class DetailFactory @AssistedInject constructor(
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
        fun create(@Assisted("nasaId") nasaId: String): DetailFactory
    }
}
