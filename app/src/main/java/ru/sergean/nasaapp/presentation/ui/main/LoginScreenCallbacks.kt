package ru.sergean.nasaapp.presentation.ui.main

import ru.sergean.nasaapp.di.login.LoginComponent

interface LoginScreenCallbacks {
    val loginComponent: LoginComponent

    fun createComponent()
    fun destroyComponent()
}