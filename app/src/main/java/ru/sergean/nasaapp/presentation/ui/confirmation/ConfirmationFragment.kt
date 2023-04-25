package ru.sergean.nasaapp.presentation.ui.confirmation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.fraggjkee.smsconfirmationview.SmsConfirmationView
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.databinding.FragmentConfirmationBinding
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationData
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationFragment
import ru.sergean.nasaapp.utils.parcelableArgs
import ru.sergean.nasaapp.utils.showSnackbar
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

    private var inputManager: InputMethodManager? = null

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
        viewModel.dispatch(ConfirmationAction.VerifyNumber(activity = requireActivity()))

        inputManager = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: $registrationData")

        binding.run {
            numberTextView.text = formattedPhoneNumber

            codeInput.onChangeListener = SmsConfirmationView.OnChangeListener { code, isComplete ->
                if (isComplete) viewModel.dispatch(ConfirmationAction.ValidateCode(code))
            }

            resendText.setOnClickListener {
                showSnackbar(R.string.try_later)
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
                    confirmProgress.isVisible = state.progress

                    resendText.isInvisible = state.progress || !state.codeSent

                    if (state.codeSent) {
                        codeInput.requestFocus()
                        inputManager?.showSoftInput(codeInput, 0)
                    }
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
                        //showSnackbar(effect.text)
                    }
                    is ConfirmationEffect.AuthError -> {
                        Log.d(TAG, "observeSideEffects: Error ${effect.exception.localizedMessage}")

                        showSnackbar(R.string.unknown_error)
                        navigateToRegistration()
                    }
                    is ConfirmationEffect.InvalidCode -> {
                        Log.d(TAG, "observeSideEffects: Invalid Code")

                        showSnackbar(R.string.invalid_code)
                        binding.codeInput.enteredCode = ""
                    }
                    is ConfirmationEffect.SuccessConfirmation -> {
                        Log.d(TAG, "observeSideEffects: Success")

                        showSnackbar(R.string.success_confirmation)
                        navigateToApp()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inputManager?.hideSoftInputFromWindow(binding.codeInput.windowToken, 0)
    }

    private fun navigateToRegistration() {
        findNavController().navigateUp()
    }

    private fun navigateToApp() {
        findNavController().navigate(R.id.action_confirmationFragment_to_homeFragment)
    }

    companion object {
        const val ARG_REG_DATA = "registration_data"
        const val ARG_FORMATTED_NUMBER = "formatted_number"
    }
}