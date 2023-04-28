package ru.sergean.nasaapp.presentation.ui.registration

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.domain.user.RegisterUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.utils.isMatch
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
) : BaseViewModel<RegistrationState, RegistrationAction, RegistrationEffect>(
    initialState = RegistrationState()
) {
    override fun dispatch(action: RegistrationAction) {
        when (action) {
            is RegistrationAction.ChangeName -> reduce(action)
            is RegistrationAction.ChangeEmail -> reduce(action)
            is RegistrationAction.ChangePhone -> reduce(action)
            is RegistrationAction.ChangePassword -> reduce(action)
        }
    }

    private fun reduce(action: RegistrationAction.ChangeName) {
        viewState = if (action.name.isNotBlank() && action.name.isNotEmpty()) {
            viewState.copy(name = action.name, nameError = null)
        } else {
            viewState.copy(name = action.name, nameError = R.string.name_error)
        }
    }

    private fun reduce(action: RegistrationAction.ChangeEmail) {
        viewState = if (Patterns.EMAIL_ADDRESS.matcher(action.email).matches()) {
            viewState.copy(email = action.email, emailError = null)
        } else {
            viewState.copy(email = action.email, emailError = R.string.email_error)
        }
    }

    private fun reduce(action: RegistrationAction.ChangePhone) {
        viewState = if (action.isValid) {
            viewState.copy(
                phone = action.phone, formattedPhone = action.formattedPhone,
                phoneError = null
            )
        } else {
            viewState.copy(
                phone = action.phone, formattedPhone = action.formattedPhone,
                phoneError = R.string.phone_error
            )
        }
    }

    private fun reduce(action: RegistrationAction.ChangePassword) {
        viewState = if (action.password.isMatch(regex = PASSWORD_PATTERN)) {
            viewState.copy(password = action.password, passwordError = null)
        } else {
            viewState.copy(password = action.password, passwordError = R.string.password_error)
        }
    }

    companion object {
        private const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,20}\$"
    }
}