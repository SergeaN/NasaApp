package ru.sergean.nasaapp.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.sergean.nasaapp.data.images.ImageRepository
import ru.sergean.nasaapp.domain.FetchImagesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State
import ru.sergean.nasaapp.presentation.ui.favorites.FavoriteImageItem
import javax.inject.Inject

data class HomeState(
    val progress: Boolean = false,
    val query: String = "",
    val news: List<ImageItem> = emptyList(),
) : State

sealed interface HomeAction : Action {
    object Refresh : HomeAction
    data class ChangeQuery(val query: String) : HomeAction
}

sealed interface HomeEffect : Effect {
    data class Message(val text: String) : HomeEffect
}


class HomeViewModel(
    private val fetchImagesUseCase: FetchImagesUseCase
) : BaseViewModel<HomeState, HomeAction, HomeEffect>(initialState = HomeState()) {

    override fun dispatch(action: HomeAction) {
        when (action) {
            is HomeAction.Refresh -> reduce(action)
            is HomeAction.ChangeQuery -> reduce(action)
        }
    }

    private fun reduce(action: HomeAction.Refresh) {
        if (viewState.progress) {
            sideEffect = HomeEffect.Message(text = "In process!")
        } else {
            fetchImages(query = viewState.query)
        }
    }

    private fun reduce(action: HomeAction.ChangeQuery) {
        if (viewState.progress) {
            sideEffect = HomeEffect.Message(text = "In process!")
        } else {
            viewState = viewState.copy(query = action.query)
        }
    }

    private fun fetchImages(query: String) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            viewState = try {
                val news = fetchImagesUseCase.invoke(query)
                viewState.copy(progress = false, news = news.map { it.mapToImageItem() })
            } catch (e: Exception) {
                sideEffect = HomeEffect.Message(text = "Unknown error")
                viewState.copy(progress = false)
            }
        }
    }

    class Factory @Inject constructor(
        private val fetchImagesUseCase: FetchImagesUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(fetchImagesUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}