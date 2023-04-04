package ru.sergean.nasaapp.presentation.ui.favorites

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
import ru.sergean.nasaapp.databinding.FragmentConfirmationBinding
import ru.sergean.nasaapp.databinding.FragmentFavoritesBinding
import ru.sergean.nasaapp.presentation.ui.detail.DetailViewModel

class FavoritesFragment :  Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels()

    private val binding by viewBinding(FragmentFavoritesBinding::bind)

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