package ru.sergean.nasaapp.presentation.ui.main

import androidx.annotation.IdRes
import ru.sergean.nasaapp.R

sealed class StartMode(@IdRes val startDestinationId: Int) {
    object ShowIntro : StartMode(R.id.introFragment)
    object SkipIntro : StartMode(R.id.loginFragment)
    object ShowApp : StartMode(R.id.homeFragment)
}
