package ru.sergean.nasaapp.presentation.ui.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.sergean.nasaapp.data.auth.RegistrationLogService
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import ru.sergean.nasaapp.domain.user.RegisterUseCase
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationData

class ConfirmationViewModelFactory @AssistedInject constructor(
    @Assisted("reg_data") private val registrationData: RegistrationData,
    private val auth: FirebaseAuth,
    private val registerUseCase: RegisterUseCase,
    private val settingDataStore: SettingDataStore,
    private val logService: RegistrationLogService,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfirmationViewModel::class.java)) {
            return ConfirmationViewModel(
                registrationData, auth, registerUseCase, settingDataStore, logService
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("reg_data") registrationData: RegistrationData
        ): ConfirmationViewModelFactory
    }
}