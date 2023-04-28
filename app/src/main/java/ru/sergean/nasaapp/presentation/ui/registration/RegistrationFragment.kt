package ru.sergean.nasaapp.presentation.ui.registration

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.Lazy
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.databinding.FragmentRegistrationBinding
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModelFactory
import ru.sergean.nasaapp.presentation.ui.confirmation.ConfirmationFragment
import ru.sergean.nasaapp.presentation.ui.main.LoginScreenCallbacks
import ru.sergean.nasaapp.utils.EditTextWatcher
import javax.inject.Inject

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory

    private val viewModel: RegistrationViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentRegistrationBinding::bind)

    private var callbacks: LoginScreenCallbacks? = null

    override fun onAttach(context: Context) {
        callbacks = (context as LoginScreenCallbacks).apply {
            createComponent()
            loginComponent.inject(fragment = this@RegistrationFragment)
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            codePicker.registerCarrierNumberEditText(phoneEditText)
            registerButton.setOnClickListener {
                navigateToConfirmation()
            }
        }

        observeState()
        observeSideEffects()
    }

    override fun onStart() {
        super.onStart()

        val nameWatcher = EditTextWatcher(lifecycleScope)
        val emailWatcher = EditTextWatcher(lifecycleScope)
        val phoneWatcher = EditTextWatcher(lifecycleScope)
        val passwordWatcher = EditTextWatcher(lifecycleScope)

        binding.run {
            nameEditText.doOnTextChanged { text, _, _, _ ->
                nameWatcher.watch(text) {
                    viewModel.dispatch(RegistrationAction.ChangeName(it))
                }
            }
            emailEditText.doOnTextChanged { text, _, _, _ ->
                emailWatcher.watch(text) {
                    viewModel.dispatch(RegistrationAction.ChangeEmail(it))
                }
            }
            phoneEditText.doOnTextChanged { text, _, _, _ ->
                phoneWatcher.watch(text) {
                    val phone = codePicker.fullNumberWithPlus
                    val formattedPhone = codePicker.formattedFullNumber
                    val isValid = codePicker.isValidFullNumber
                    viewModel.dispatch(
                        RegistrationAction.ChangePhone(phone, formattedPhone, isValid)
                    )
                }
            }
            passwordEditText.doOnTextChanged { text, _, _, _ ->
                passwordWatcher.watch(text) {
                    viewModel.dispatch(RegistrationAction.ChangePassword(it))
                }
            }
        }
    }


    private fun observeState() {
        lifecycleScope.launch {
            viewModel.observeState().flowWithLifecycle(lifecycle).collect { state ->
                Log.d(TAG, "observeState: $state")
                binding.run {
                    nameInput.isErrorEnabled = state.nameError != null
                    emailInput.isErrorEnabled = state.emailError != null
                    phoneInput.isErrorEnabled = state.phoneError != null
                    passwordInput.isErrorEnabled = state.passwordError != null

                    nameInput.error = state.nameError?.let { getString(it) }
                    emailInput.error = state.emailError?.let { getString(it) }
                    phoneInput.error = state.phoneError?.let { getString(it) }
                    passwordInput.error = state.passwordError?.let { getString(it) }

                    registerButton.isEnabled = state.isDataValid
                }
            }
        }
    }

    private fun observeSideEffects() {
        lifecycleScope.launch {
            viewModel.observeSideEffect().flowWithLifecycle(lifecycle).collect { effect ->
                when (effect) {
                    is RegistrationEffect.Message -> {
                        Log.d(TAG, "observeSideEffects: Message ${effect.text}")
                    }
                    is RegistrationEffect.SuccessSignUp -> {
                        Log.d(TAG, "observeSideEffects: SuccessSignUp ${effect.token}")
                    }
                }
            }
        }
    }

    private fun navigateToConfirmation() {
        val registrationData = viewModel.observeState().value.run {
            RegistrationData(
                name = name ?: "", email = email ?: "",
                phoneNumber = phone ?: "", password = password ?: ""
            )
        }

        val formattedPhoneNumber = viewModel.observeState().value.formattedPhone

        val args = bundleOf(
            ConfirmationFragment.ARG_REG_DATA to registrationData,
            ConfirmationFragment.ARG_FORMATTED_NUMBER to formattedPhoneNumber,
        )

        findNavController().navigate(R.id.action_registrationFragment_to_confirmationFragment, args)
    }
}