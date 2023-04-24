package ru.sergean.nasaapp.presentation.ui.login

import androidx.annotation.StringRes
import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State

data class LoginState(
    val progress: Boolean = false,
    val email: String? = null,
    val password: String? = null,
    @StringRes val emailError: Int? = null, // null means valid
    @StringRes val passwordError: Int? = null, // null means valid
) : State

val LoginState.isDataValid
    get() = emailError == null && passwordError == null
            && email != null && password != null

sealed interface LoginAction : Action {
    object SignIn : LoginAction
    data class ChangeEmail(val email: String) : LoginAction
    data class ChangePassword(val password: String) : LoginAction
}

sealed interface LoginEffect : Effect {
    data class Message(@StringRes val text: Int) : LoginEffect
    data class SuccessSigIn(val token: String) : LoginEffect
}



