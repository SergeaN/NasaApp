package ru.sergean.nasaapp.presentation.ui.home

import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State
import ru.sergean.nasaapp.presentation.ui.home.items.ImageItem

data class HomeState(
    val progress: Boolean = false,
    val query: String = "",
    val images: List<ImageItem> = emptyList(),
) : State

sealed interface HomeAction : Action {
    data class Refresh(val force: Boolean = false) : HomeAction
    data class ChangeQuery(val query: String) : HomeAction
}

sealed interface HomeEffect : Effect {
    data class Message(val text: String) : HomeEffect
}