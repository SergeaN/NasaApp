package ru.sergean.nasaapp.presentation.ui.detail

import androidx.annotation.StringRes
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State

data class DetailState(
    val progress: Boolean = false,
    val imageSaved: Boolean = false,
    val image: ImageModel? = null
) : State

sealed interface DetailAction : Action {
    object GetImage: DetailAction
    object ChangeSaving : DetailAction
}

sealed interface DetailEffect : Effect {
    data class Message(@StringRes val text: Int) : DetailEffect
}