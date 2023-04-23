package ru.sergean.nasaapp.presentation.ui.confirmation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.fraggjkee.smsconfirmationview.SmsConfirmationView
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.databinding.FragmentConfirmationBinding
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationData
import ru.sergean.nasaapp.utils.parcelableArgs
import ru.sergean.nasaapp.utils.stringArgs
import javax.inject.Inject

class ConfirmationFragment : Fragment(R.layout.fragment_confirmation) {

    @Inject
    lateinit var viewModelFactory: ConfirmationViewModelFactory.Factory

    private val registrationData: RegistrationData by parcelableArgs(ARG_REG_DATA)
    private val formattedPhoneNumber by stringArgs(ARG_FORMATTED_NUMBER)

    private val viewModel: ConfirmationViewModel by viewModels {
        viewModelFactory.create(registrationData)
    }

    private val binding by viewBinding(FragmentConfirmationBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
        //viewModel.dispatch(ConfirmationAction.VerifyNumber(activity = requireActivity()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: $registrationData")

        binding.run {
            numberTextView.text = formattedPhoneNumber

            codeInput.onChangeListener = SmsConfirmationView.OnChangeListener { code, isComplete ->
                if (isComplete) viewModel.dispatch(ConfirmationAction.ValidateCode(code))
            }
        }

        observeState()
        observeSideEffects()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.observeState().flowWithLifecycle(lifecycle).collect { state ->
                Log.d(TAG, "observeState: $state")
                binding.run {
                    state.message?.let { confirmInfoText.text = getString(it) }
                    codeInput.isEnabled = !state.progress
                    resendButton.isEnabled = !state.progress
                    confirmProgress.isVisible = state.progress
                }
            }
        }
    }

    private fun observeSideEffects() {
        lifecycleScope.launch {
            viewModel.observeSideEffect().flowWithLifecycle(lifecycle).collect { effect ->
                Log.d(TAG, "observeSideEffects: $effect")
                when (effect) {
                    is ConfirmationEffect.Message -> {
                        Log.d(TAG, "observeSideEffects: ${effect.text}")
                    }
                    is ConfirmationEffect.AuthError -> {
                        Log.d(TAG, "observeSideEffects: Error ${effect.exception.localizedMessage}")
                    }
                    is ConfirmationEffect.InvalidCode -> {
                        Log.d(TAG, "observeSideEffects: Invalid Code")
                        binding.codeInput.enteredCode = ""
                    }
                    is ConfirmationEffect.SuccessConfirmation -> {
                        Log.d(TAG, "observeSideEffects: Success")
                    }
                }
            }
        }
    }

    companion object {
        const val ARG_REG_DATA = "registration_data"
        const val ARG_FORMATTED_NUMBER = "formatted_number"
    }
}