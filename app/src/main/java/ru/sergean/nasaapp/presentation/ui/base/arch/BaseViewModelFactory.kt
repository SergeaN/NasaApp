package ru.sergean.nasaapp.presentation.ui.base.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class BaseViewModelFactory @Inject constructor(
    private val viewModelFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelFactories.getValue(modelClass as Class<ViewModel>).get() as T
    }

    val viewModelClasses get() = viewModelFactories.keys
}
