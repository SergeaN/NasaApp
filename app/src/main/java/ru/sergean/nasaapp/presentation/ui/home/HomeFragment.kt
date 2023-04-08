package ru.sergean.nasaapp.presentation.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.databinding.FragmentHomeBinding
import ru.sergean.nasaapp.presentation.ui.base.adapter.FingerprintAdapter
import ru.sergean.nasaapp.presentation.ui.base.adapter.Item
import ru.sergean.nasaapp.presentation.ui.favorites.FavoriteImageItem
import ru.sergean.nasaapp.presentation.ui.favorites.FavoritesAction
import ru.sergean.nasaapp.presentation.ui.favorites.FavoritesEffect
import ru.sergean.nasaapp.presentation.ui.favorites.FavoritesImageItemFingerprint

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val items: MutableList<Item> = mutableListOf()

    private var adapter: FingerprintAdapter? = null


    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FingerprintAdapter(getFingerprints())

        viewModel.dispatch(HomeAction.Refresh)

        observeState()
        observeSideEffects()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.observeState().collect {
                Log.d(TAG, "observeState: $it")
            }
        }
    }

    private fun observeSideEffects() {
        lifecycleScope.launch {
            viewModel.observeSideEffect().collect {
                when (it) {
                    is HomeEffect.Message -> Log.d(TAG, "observeSideEffects: ${it.text}")
                }
            }
        }
    }

    private fun getFingerprints() = listOf(
        ImageItemFingerprint(::onImageClick)
    )

    private fun onImageClick(imageItem: ImageItem) {
        Log.d(TAG, "onImageClick: $imageItem")
        Toast.makeText(requireContext(), imageItem.imageUrl, Toast.LENGTH_LONG).show()
    }


}