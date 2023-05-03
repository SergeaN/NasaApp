package ru.sergean.nasaapp.presentation.ui.confirmation

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.data.auth.RegistrationLogService
import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.data.datastore.SettingDataStore
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
            is ConfirmationAction.ResendCode -> reduce(action)
            is ConfirmationAction.ValidateCode -> reduce(action)
        }
    }

    private fun reduce(action: ConfirmationAction.VerifyNumber) {
        when {
            viewState.progress -> sideEffect = ConfirmationEffect.Message(R.string.in_process)
            viewState.codeSent -> sideEffect = ConfirmationEffect.Message(R.string.already_sent)
            else -> {
                viewState = viewState.copy(progress = true, message = R.string.sending_code)
                startVerification(action.activity)
            }
        }
    }

    private fun reduce(action: ConfirmationAction.ResendCode) {
        when {
            viewState.progress -> sideEffect = ConfirmationEffect.Message(R.string.in_process)
            !viewState.codeSent -> sideEffect = ConfirmationEffect.Message(R.string.in_process)
            resendToken == null -> sideEffect = ConfirmationEffect.Message(R.string.resend_error)
            else -> {
                viewState = viewState.copy(
                    progress = true, codeSent = false, message = R.string.sending_code
                )
                resendVerificationCode(action.activity, resendToken!!)
            }
        }
    }

    private fun reduce(action: ConfirmationAction.ValidateCode) {
        when {
            viewState.progress -> {
                sideEffect = ConfirmationEffect.Message(R.string.in_process)
            }
            storedVerificationId == null -> {
                sideEffect = ConfirmationEffect.AuthError(Exception("StoredVerificationId is null"))
            }
            else -> {
                viewState = viewState.copy(progress = true, message = R.string.confirm_code)
                verifyPhoneNumberWithCode(storedVerificationId!!, action.code)
            }
        }
    }

    private fun startVerification(activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(registrationData.phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(
        activity: Activity, token: PhoneAuthProvider.ForceResendingToken
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(registrationData.phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        optionsBuilder.setForceResendingToken(token)
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
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

    private fun verifyPhoneNumberWithCode(storedVerificationId: String, code: String) {
        Log.d(TAG, "verifyPhoneNumberWithCode: ")
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
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
        withViewModelScope {
            val result = registerUseCase.invoke(
                registrationData.name, registrationData.email,
                registrationData.phoneNumber, registrationData.password
            )

            sideEffect = when (result) {
                is ResultWrapper.Success -> {
                    logService.userRegistered()
                    settingDataStore.login()

                    ConfirmationEffect.SuccessConfirmation
                }
                is ResultWrapper.Failure -> {
                    ConfirmationEffect.AuthError(Exception(result.message))
                }
            }
        }
    }
}