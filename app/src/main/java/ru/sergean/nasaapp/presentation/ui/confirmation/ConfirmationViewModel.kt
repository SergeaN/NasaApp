package ru.sergean.nasaapp.presentation.ui.confirmation

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.data.auth.RegistrationLogService
import ru.sergean.nasaapp.data.datastore.SettingDataStore
import ru.sergean.nasaapp.data.user.RegisterResult
import ru.sergean.nasaapp.data.user.UserService
import ru.sergean.nasaapp.domain.user.RegisterUseCase
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationData
import java.util.concurrent.TimeUnit

class ConfirmationViewModel(
    private val registrationData: RegistrationData,
    private val auth: FirebaseAuth,
    private val registerUseCase: RegisterUseCase,
    private val settingDataStore: SettingDataStore,
    private val logService: RegistrationLogService,
) : BaseViewModel<ConfirmationState, ConfirmationAction, ConfirmationEffect>(
    initialState = ConfirmationState()
) {

    private var storedVerificationId: String? = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    init {
        registrationData.run {
            logService.userEnteredData(name, email, phoneNumber, password)
        }
    }

    override fun dispatch(action: ConfirmationAction) {
        Log.d(TAG, "dispatch: $action")
        when (action) {
            is ConfirmationAction.VerifyNumber -> reduce(action)
            is ConfirmationAction.ValidateCode -> reduce(action)
        }
    }

    private fun reduce(action: ConfirmationAction.VerifyNumber) {
        when {
            viewState.progress -> sideEffect = ConfirmationEffect.Message(R.string.in_process)
            viewState.codeSent -> sideEffect = ConfirmationEffect.Message(R.string.already_sent)
            else -> {
                viewState = viewState.copy(
                    progress = true, message = R.string.sending_code
                )
                startVerification(action.activity)
            }
        }
    }

    private fun reduce(action: ConfirmationAction.ValidateCode) {
        if (viewState.progress) {
            sideEffect = ConfirmationEffect.Message(R.string.in_process)
        } else {
            viewState = viewState.copy(progress = true, message = R.string.confirm_code)
            verifyPhoneNumberWithCode(action.code)
        }
    }

    private fun startVerification(activity: Activity) {
        withViewModelScope {
            Log.d(TAG, "startVerification: ${registrationData.phoneNumber}")
            delay(timeMillis = 5000)
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(registrationData.phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationAutoCompleted: $credential")

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, "onVerificationFailed", e)

            sideEffect = ConfirmationEffect.AuthError(Exception(e))
        }

        override fun onCodeSent(
            verificationId: String, token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:${verificationId.take(n = 10)}")

            storedVerificationId = verificationId
            resendToken = token

            viewState = viewState.copy(
                progress = false, codeSent = true, message = R.string.we_sent_code_to
            )
        }
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        storedVerificationId?.let {
            Log.d(TAG, "verifyPhoneNumberWithCode: ")
            val credential = PhoneAuthProvider.getCredential(it, code)
            signInWithPhoneAuthCredential(credential)
        } ?: {
            sideEffect = ConfirmationEffect.AuthError(Exception("StoredVerificationId is null"))
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    Log.d(TAG, "Validation: Success")

                    logService.userConfirmed()
                    register()
                }
                task.exception is FirebaseAuthInvalidCredentialsException -> {
                    Log.d(TAG, "Validation: Invalid Code")

                    viewState = viewState.copy(progress = false)
                    sideEffect = ConfirmationEffect.InvalidCode
                }
                else -> {
                    Log.d(TAG, "Validation: Error ${task.exception?.localizedMessage}")

                    sideEffect = ConfirmationEffect.AuthError(Exception(task.exception))
                }
            }
        }
    }

    private fun register() {
        Log.d(TAG, "Start register: $registrationData")
        withViewModelScope {
            sideEffect = try {
                val result = registerUseCase.invoke(
                    registrationData.name, registrationData.email,
                    registrationData.phoneNumber, registrationData.password
                )
                Log.d(TAG, "register: $result")
                when (result) {
                    is RegisterResult.Success -> {
                        Log.d(TAG, "register: Success")

                        logService.userRegistered()
                        settingDataStore.login()

                        ConfirmationEffect.SuccessConfirmation
                    }
                    is RegisterResult.Error -> {
                        Log.d(TAG, "register: Error")

                        ConfirmationEffect.AuthError(Exception(result.exception))
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "register: ${e.localizedMessage}")
                ConfirmationEffect.AuthError(e)
            }
        }

    }
}

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