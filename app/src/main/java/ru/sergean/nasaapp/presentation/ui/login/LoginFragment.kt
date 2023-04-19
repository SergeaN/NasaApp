package ru.sergean.nasaapp.presentation.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.databinding.FragmentLoginBinding
import ru.sergean.nasaapp.utils.EditTextWatcher
import ru.sergean.nasaapp.utils.showSnackbar
import javax.inject.Inject

class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var viewModelFactory: LoginViewModel.Factory

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Login")

        binding.run {
            loginButton.setOnClickListener { viewModel.dispatch(LoginAction.SignIn) }
            registerButton.setOnClickListener { navigateToRegistration() }
        }

        observeState()
        observeSideEffects()
    }

    override fun onStart() {
        super.onStart()

        val emailWatcher = EditTextWatcher(lifecycleScope)
        val passwordWatcher = EditTextWatcher(lifecycleScope)

        binding.run {
            emailEditText.doOnTextChanged { text, _, _, _ ->
                emailWatcher.watch(text) { viewModel.dispatch(LoginAction.ChangeEmail(it)) }
            }
            passwordEditText.doOnTextChanged { text, _, _, _ ->
                passwordWatcher.watch(text) { viewModel.dispatch(LoginAction.ChangePassword(it)) }
            }
        }
    }

    private fun observeState() {
        Log.d(TAG, "observeState Login")
        lifecycleScope.launch {
            viewModel.observeState().flowWithLifecycle(lifecycle).collect { state ->
                Log.d(TAG, "observeState: $state")
                binding.run {
                    emailInput.error = state.emailError?.let { getString(it) }
                    passwordInput.error = state.passwordError?.let { getString(it) }

                    loginProgress.isVisible = state.progress

                    emailInput.isEnabled = !state.progress
                    passwordInput.isEnabled = !state.progress

                    registerButton.isInvisible = state.progress
                    loginButton.isInvisible = state.progress

                    loginButton.isEnabled = state.isDataValid
                }
            }
        }
    }

    private fun observeSideEffects() {
        lifecycleScope.launch {
            viewModel.observeSideEffect().flowWithLifecycle(lifecycle).collect { effect ->
                when (effect) {
                    is LoginEffect.Message -> showSnackbar(effect.text)
                    is LoginEffect.SuccessSigIn -> navigateToApp(effect.token)
                }
            }
        }
    }


    private fun navigateToRegistration() {
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }

    private fun navigateToApp(token: String) {
        Log.d(TAG, "navigateToApp: $token")
        val arguments = bundleOf("token" to token)
        findNavController().navigate(R.id.action_loginFragment_to_favoritesFragment, arguments)
    }

}