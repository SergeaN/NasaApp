package ru.sergean.nasaapp.presentation.ui.login

import android.util.Patterns
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import ru.sergean.nasaapp.data.user.PASSWORD_PATTERN
import ru.sergean.nasaapp.domain.user.LoginUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.utils.isMatch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val settingDataStore: SettingDataStore,
) : BaseViewModel<LoginState, LoginAction, LoginEffect>(
    initialState = LoginState()
) {
    override fun dispatch(action: LoginAction) {
        when (action) {
            is LoginAction.SignIn -> reduce(action)
            is LoginAction.ChangeEmail -> reduce(action)
            is LoginAction.ChangePassword -> reduce(action)
        }
    }

    private fun reduce(action: LoginAction.SignIn) {
        signIn(email = viewState.email ?: "", password = viewState.password ?: "")
    }

    private fun reduce(action: LoginAction.ChangeEmail) {
        viewState = if (Patterns.EMAIL_ADDRESS.matcher(action.email).matches()) {
            viewState.copy(email = action.email, emailError = null)
        } else {
            viewState.copy(email = action.email, emailError = R.string.email_error)
        }
    }

    private fun reduce(action: LoginAction.ChangePassword) {
        viewState = if (action.password.isMatch(regex = PASSWORD_PATTERN)) {
            viewState.copy(password = action.password, passwordError = null)
        } else {
            viewState.copy(password = action.password, passwordError = R.string.password_error)
        }
    }

    private fun signIn(email: String, password: String) {
        viewState = viewState.copy(progress = true)
        withViewModelScope {
            sideEffect = when (val result = loginUseCase(email, password)) {
                is ResultWrapper.Success -> {
                    launch { settingDataStore.login() }
                    LoginEffect.SuccessSigIn(result.data.token)
                }
                is ResultWrapper.Failure -> {
                    viewState = viewState.copy(progress = false)
                    LoginEffect.Message(text = R.string.unknown_error)
                }
            }
        }
    }
}