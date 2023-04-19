package ru.sergean.nasaapp.presentation.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import ru.sergean.nasaapp.data.user.LoginResult
import ru.sergean.nasaapp.domain.user.LoginUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.utils.isMatch
import javax.inject.Inject

class LoginViewModel(
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
        signIn(email = viewState.email, password = viewState.password)
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
        withViewModelScope {
            viewState = viewState.copy(progress = true)
            try {
                Log.d(TAG, "signIn: $email $password")
                delay(timeMillis = 5000)
                Log.d(TAG, "signIn")
                sideEffect = when (val result = loginUseCase(email, password)) {
                    is LoginResult.Success -> {
                        Log.d(TAG, "signIn: Success")
                        launch { settingDataStore.login() }
                        LoginEffect.SuccessSigIn(result.token)
                    }
                    is LoginResult.Error -> {
                        Log.d(TAG, "signIn: Error")
                        viewState = viewState.copy(progress = false)
                        LoginEffect.Message(text = R.string.unknown_error)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "signIn: Error ${e.localizedMessage}")
                sideEffect = LoginEffect.Message(text = R.string.unknown_error)
                viewState = viewState.copy(progress = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

    companion object {
        private const val PASSWORD_PATTERN = ".{4,20}\$"
    }

    class Factory @Inject constructor(
        private val loginUseCase: LoginUseCase,
        private val settingDataStore: SettingDataStore,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(loginUseCase, settingDataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}