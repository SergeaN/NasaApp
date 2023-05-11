package ru.sergean.nasaapp.presentation.ui.registration

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.sergean.nasaapp.presentation.ui.base.arch.Action
import ru.sergean.nasaapp.presentation.ui.base.arch.Effect
import ru.sergean.nasaapp.presentation.ui.base.arch.State

@Parcelize
data class RegistrationData(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
) : Parcelable

data class RegistrationState(
    val progress: Boolean = false,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val formattedPhone: String = "",
    val password: String? = null,
    @StringRes val nameError: Int? = null,  // null means valid
    @StringRes val emailError: Int? = null,  // null means valid
    @StringRes val phoneError: Int? = null,  // null means valid
    @StringRes val passwordError: Int? = null,  // null means valid
) : State

val RegistrationState.isDataValid: Boolean
    get() = nameError == null && emailError == null && phoneError == null && passwordError == null
            && name != null && email != null && phone != null && password != null

sealed interface RegistrationAction : Action {
    data class ChangeName(val name: String) : RegistrationAction
    data class ChangeEmail(val email: String) : RegistrationAction
    data class ChangePhone(
        val phone: String, val formattedPhone: String, val isValid: Boolean
    ) : RegistrationAction

    data class ChangePassword(val password: String) : RegistrationAction
}

sealed interface RegistrationEffect : Effect {
    data class Message(@StringRes val text: Int) : RegistrationEffect
}

