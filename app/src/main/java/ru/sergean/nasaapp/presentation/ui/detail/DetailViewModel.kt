package ru.sergean.nasaapp.presentation.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.domain.AddToFavoritesUseCase
import ru.sergean.nasaapp.domain.GetImageUseCase
import ru.sergean.nasaapp.domain.RemoveFromFavoritesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State

data class DetailState(
    val progress: Boolean = false,
    val imageSaved: Boolean = false,
    val image: ImageModel? = null
) : State

sealed interface DetailAction : Action {
    object ChangeSaving : DetailAction
}

sealed interface DetailEffect : Effect {
    data class Message(val text: String) : DetailEffect
}

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
        Log.d(TAG, "getImage: $nasaId")
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            viewState = try {
                val image = getImageUseCase.invoke(nasaId)
                Log.d(TAG, "getImage: $image")
                viewState.copy(
                    progress = false, image = image,
                    imageSaved = image?.isFavorite ?: false
                )
            } catch (e: Exception) {
                Log.d(TAG, "getImage: $e")
                sideEffect = DetailEffect.Message(text = "Unknown Error")
                viewState.copy(progress = false, imageSaved = false)
            }
        }
    }

    private fun reduce(action: DetailAction.ChangeSaving) {
        Log.d(TAG, "reduce: ${viewState.imageSaved}")
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
                Log.d(TAG, "changeSaving...")
                viewState = viewState.copy(progress = false, imageSaved = !viewState.imageSaved)
            } catch (e: Exception) {
                Log.e(TAG, "changeSaving: ", e)
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
