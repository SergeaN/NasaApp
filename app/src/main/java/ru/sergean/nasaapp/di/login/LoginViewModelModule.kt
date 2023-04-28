package ru.sergean.nasaapp.di.login

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.sergean.nasaapp.di.app.ViewModelKey
import ru.sergean.nasaapp.presentation.ui.login.LoginViewModel
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationViewModel

@Module
interface LoginViewModelModule {

    @Binds
    @[IntoMap ViewModelKey(LoginViewModel::class)]
    fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(RegistrationViewModel::class)]
    fun bindRegistrationViewModel(registrationViewModel: RegistrationViewModel): ViewModel

}