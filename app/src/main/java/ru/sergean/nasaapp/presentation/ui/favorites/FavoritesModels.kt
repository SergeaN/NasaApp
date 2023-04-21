package ru.sergean.nasaapp.presentation.ui.favorites

import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State
import ru.sergean.nasaapp.presentation.ui.favorites.items.FavoriteImageItem

data class FavoritesState(
    val progress: Boolean = false,
    val query: String = "",
    val images: List<FavoriteImageItem> = emptyList(),
) : State

sealed interface FavoritesAction : Action {
    object  Refresh : FavoritesAction
    data class ChangeQuery(val query: String) : FavoritesAction
    data class RemoveFromFavorites(val nasaId: String): FavoritesAction
}

sealed interface FavoritesEffect : Effect {
    data class Message(val text: String) : FavoritesEffect
}