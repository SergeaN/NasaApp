package ru.sergean.nasaapp.presentation.ui.confirmation

import android.app.Activity
import androidx.annotation.StringRes
import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State

data class ConfirmationState(
    val progress: Boolean = false,
    val codeSent: Boolean = false,
    @StringRes val message: Int? = null
) : State

sealed interface ConfirmationAction : Action {
    data class VerifyNumber(val activity: Activity) : ConfirmationAction
    data class ResendCode(val activity: Activity): ConfirmationAction
    data class ValidateCode(val code: String) : ConfirmationAction
}

sealed interface ConfirmationEffect : Effect {
    data class Message(@StringRes val text: Int) : ConfirmationEffect
    data class AuthError(val exception: Exception) : ConfirmationEffect
    object InvalidCode : ConfirmationEffect
    object SuccessConfirmation : ConfirmationEffect
}