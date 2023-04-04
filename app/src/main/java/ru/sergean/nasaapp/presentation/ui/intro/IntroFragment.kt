package ru.sergean.nasaapp.presentation.ui.intro

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.databinding.FragmentFavoritesBinding
import ru.sergean.nasaapp.databinding.FragmentIntroBinding
import ru.sergean.nasaapp.presentation.ui.favorites.FavoritesViewModel

class IntroFragment : Fragment(R.layout.fragment_intro) {

    private val viewModel: IntroViewModel by viewModels()

    private val binding by viewBinding(FragmentIntroBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {

        }

        viewModel.run {

        }
    }

}