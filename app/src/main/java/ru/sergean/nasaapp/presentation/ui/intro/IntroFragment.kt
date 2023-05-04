package ru.sergean.nasaapp.presentation.ui.intro

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.databinding.FragmentIntroBinding
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModelFactory
import ru.sergean.nasaapp.utils.onPageSelected
import javax.inject.Inject

class IntroFragment : Fragment(R.layout.fragment_intro) {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory

    private val viewModel: IntroViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentIntroBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = savedInstanceState?.getInt(EXTRA_POSITION) ?: 0
        viewModel.updatePosition(position)

        binding.run {
            viewPager.adapter = IntroAdapter(viewModel.data, fragment = this@IntroFragment)
            dotsIndicator.attachTo(viewPager)
            viewPager.onPageSelected { position -> viewModel.updatePosition(position) }
        }

        observePosition()
    }

    private fun observePosition() {
        lifecycleScope.launch {
            viewModel.currentPosition.flowWithLifecycle(lifecycle).collect { position ->
                binding.run {
                    val (text, onClick) = if (position == viewModel.data.lastIndex) {
                        val navigateToLogin = {
                            viewModel.introShowed()
                            navigateToLogin()
                        }
                        getString(R.string.continue_word) to navigateToLogin
                    } else {
                        val changePosition = {
                            viewPager.setCurrentItem(position + 1, true)
                        }
                        getString(R.string.next) to changePosition
                    }
                    nextButton.text = text
                    nextButton.setOnClickListener { onClick() }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_POSITION, viewModel.currentPosition.value)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_introFragment_to_loginFragment)
    }

    companion object {
        private const val EXTRA_POSITION = "current_position"
    }
}