package ru.sergean.nasaapp.di.login

import dagger.Subcomponent
import ru.sergean.nasaapp.presentation.ui.confirmation.ConfirmationFragment
import ru.sergean.nasaapp.presentation.ui.login.LoginFragment
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationFragment
import javax.inject.Scope

@LoginScope
@Subcomponent(modules = [LoginModule::class, LoginViewModelModule::class])
interface LoginComponent {

    fun inject(fragment: LoginFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: ConfirmationFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): LoginComponent
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginScope