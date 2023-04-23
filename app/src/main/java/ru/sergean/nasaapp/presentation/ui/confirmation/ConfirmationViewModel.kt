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
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModel
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationData
import java.util.concurrent.TimeUnit

class ConfirmationViewModel(
    private val registrationData: RegistrationData,
    private val auth: FirebaseAuth,
) : BaseViewModel<ConfirmationState, ConfirmationAction, ConfirmationEffect>(
    initialState = ConfirmationState()
) {

    private var storedVerificationId: String? = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun dispatch(action: ConfirmationAction) {
        Log.d(TAG, "dispatch: $action")
        when (action) {
            is ConfirmationAction.VerifyNumber -> reduce(action)
            is ConfirmationAction.ValidateCode -> reduce(action)
        }
    }

    private fun reduce(action: ConfirmationAction.VerifyNumber) {
        if (viewState.progress) {
            sideEffect = ConfirmationEffect.Message(R.string.in_process)
        } else {
            viewState = viewState.copy(progress = true, message = R.string.sending_code)
            startVerification(action.activity)
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
        Log.d(TAG, "startVerification: ${registrationData.phoneNumber}")

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(registrationData.phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
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

            viewState = viewState.copy(progress = false, message = R.string.we_sent_code_to)
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            sideEffect = when {
                task.isSuccessful -> {
                    Log.d(TAG, "Validation: Success")
                    ConfirmationEffect.SuccessConfirmation
                }
                task.exception is FirebaseAuthInvalidCredentialsException -> {
                    Log.d(TAG, "Validation: Invalid Code")
                    ConfirmationEffect.Message(R.string.invalid_code)
                }
                else -> {
                    Log.d(TAG, "Validation: Error ${task.exception?.localizedMessage}")
                    ConfirmationEffect.AuthError(Exception(task.exception))
                }
            }
        }
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        storedVerificationId?.let {
            val credential = PhoneAuthProvider.getCredential(it, code)
            signInWithPhoneAuthCredential(credential)
        } ?: {
            sideEffect = ConfirmationEffect.AuthError(Exception("StoredVerificationId is null"))
        }
    }
}

class ConfirmationViewModelFactory @AssistedInject constructor(
    @Assisted("reg_data") private val registrationData: RegistrationData,
    private val auth: FirebaseAuth,
) : ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfirmationViewModel::class.java)) {
            return ConfirmationViewModel(
                registrationData, auth
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