package ru.sergean.nasaapp.presentation.ui.intro

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
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

        viewModel.currentPosition = savedInstanceState?.getInt(EXTRA_POSITION) ?: 0

        binding.run {
            viewPager.adapter = IntroAdapter(viewModel.data, fragment = this@IntroFragment)
            dotsIndicator.attachTo(viewPager)

            nextButton.setOnClickListener {
                viewPager.setCurrentItem(viewModel.currentPosition + 1, true)
            }

            viewPager.onPageSelected { position ->
                viewModel.currentPosition = position

                if (position == viewModel.data.size - 1) {
                    nextButton.text = getString(R.string.continue_word)
                    nextButton.setOnClickListener {
                        viewModel.introShowed()
                        navigateToLogin()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_POSITION, viewModel.currentPosition)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_introFragment_to_loginFragment)
    }

    companion object {
        private const val EXTRA_POSITION = "current_position"
    }
}