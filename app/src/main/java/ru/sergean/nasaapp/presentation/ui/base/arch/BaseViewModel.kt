package ru.sergean.nasaapp.presentation.ui.base.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : State, A : Action, E : Effect>(
    initialState: S
) : ViewModel(), Store<S, A, E> {

    private val _viewState = MutableStateFlow(initialState)

    private val _sideEffect =
        MutableSharedFlow<E>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    protected var viewState: S
        get() = _viewState.value
        set(value) {
            _viewState.value = value
        }

    protected var sideEffect: E
        get() = _sideEffect.replayCache.last()
        set(value) {
            _sideEffect.tryEmit(value)
        }

    override fun observeState(): StateFlow<S> = _viewState.asStateFlow()
    override fun observeSideEffect(): Flow<E> = _sideEffect

    protected fun withViewModelScope(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(block = block)
    }
}