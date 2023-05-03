package ru.sergean.nasaapp.presentation.ui.home

import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.domain.images.FetchImagesUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.home.items.mapToImageItem
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val fetchImagesUseCase: FetchImagesUseCase
) : BaseViewModel<HomeState, HomeAction, HomeEffect>(initialState = HomeState()) {

    override fun dispatch(action: HomeAction) {
        when (action) {
            is HomeAction.Refresh -> reduce(action)
            is HomeAction.ChangeQuery -> reduce(action)
        }
    }

    private fun reduce(action: HomeAction.Refresh) {
        when {
            viewState.progress -> {
                sideEffect = HomeEffect.Message(text = R.string.in_process)
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
        when {
            viewState.progress -> {
                sideEffect = HomeEffect.Message(text = R.string.in_process)
            }
            else -> {
                val query = action.query.dropWhile { it == ' ' }.dropLastWhile { it == ' ' }
                if (query != viewState.query) {
                    viewState = viewState.copy(query = query)
                    fetchImages(viewState.query)
                }
            }
        }
    }

    private fun fetchImages(query: String) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            viewState = when (val result = fetchImagesUseCase.invoke(query)) {
                is ResultWrapper.Success -> {
                    val images = result.data
                    viewState.copy(progress = false, images = images.map { it.mapToImageItem() })
                }
                is ResultWrapper.Failure -> {
                    sideEffect = HomeEffect.Message(text = R.string.unknown_error)
                    viewState.copy(progress = false)
                }
            }
        }
    }
}