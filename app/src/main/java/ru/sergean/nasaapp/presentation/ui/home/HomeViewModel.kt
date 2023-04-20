package ru.sergean.nasaapp.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.domain.images.FetchImagesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.home.items.mapToImageItem
import javax.inject.Inject

class HomeViewModel(
    private val fetchImagesUseCase: FetchImagesUseCase
) : BaseViewModel<HomeState, HomeAction, HomeEffect>(initialState = HomeState()) {

    override fun dispatch(action: HomeAction) {
        Log.d(TAG, "Home - dispatch: $action")
        when (action) {
            is HomeAction.Refresh -> reduce(action)
            is HomeAction.ChangeQuery -> reduce(action)
        }
    }

    private fun reduce(action: HomeAction.Refresh) {
        when {
            viewState.progress -> {
                sideEffect = HomeEffect.Message(text = "In process!")
            }
            action.force -> {
                fetchImages(query = viewState.query)
            }
            viewState.images.isEmpty() && viewState.query.isEmpty() -> {
                fetchImages(query = viewState.query)
            }
        }
    }

    private fun reduce(action: HomeAction.ChangeQuery) {
        if (viewState.progress) {
            sideEffect = HomeEffect.Message(text = "In process!")
        } else {
            val query = action.query.dropWhile { it == ' ' }.dropLastWhile { it == ' ' }
            Log.d(TAG, "reduce: $query")

            if (query != viewState.query) {
                viewState = viewState.copy(query = query)
                fetchImages(viewState.query)
            }
        }
    }

    private fun fetchImages(query: String) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            viewState = try {
                val images = fetchImagesUseCase.invoke(query)
                Log.d(TAG, "fetchImages: ${images.size}")
                viewState.copy(progress = false, images = images.map { it.mapToImageItem() })
            } catch (e: Exception) {
                sideEffect = HomeEffect.Message(text = "Unknown error")
                Log.e(TAG, "HOME fetchImages:", e)
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