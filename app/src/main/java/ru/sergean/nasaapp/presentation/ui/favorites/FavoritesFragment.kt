package ru.sergean.nasaapp.presentation.ui.favorites

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.databinding.FragmentFavoritesBinding
import ru.sergean.nasaapp.presentation.ui.base.adapter.FingerprintAdapter
import ru.sergean.nasaapp.presentation.ui.base.adapter.Item
import ru.sergean.nasaapp.presentation.ui.home.ImageItemFingerprint

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels()

    private val binding by viewBinding(FragmentFavoritesBinding::bind)

    private val items: MutableList<Item> = mutableListOf()

    private var adapter: FingerprintAdapter? = null

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FingerprintAdapter(getFingerprints())

        viewModel.dispatch(FavoritesAction.Refresh)

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
                    is FavoritesEffect.Message -> Log.d(TAG, "observeSideEffects: ${it.text}")
                }
            }
        }
    }

    private fun getFingerprints() = listOf(
        FavoritesImageItemFingerprint(::onImageClick, ::onSaveImage)
    )

    private fun onImageClick(imageItem: FavoriteImageItem) {
        Log.d(TAG, "onImageClick: $imageItem")
        Toast.makeText(requireContext(), imageItem.imageUrl, Toast.LENGTH_LONG).show()
    }

    private fun onSaveImage(imageItem: FavoriteImageItem) {
        Log.d(TAG, "onSaveImage: $imageItem")
        val postIndex = items.indexOf(imageItem)
        val newItem = imageItem.copy(isSaved = imageItem.isSaved.not())

        items.removeAt(postIndex)
        items.add(postIndex, newItem)
        adapter?.submitList(items.toList())
    }


}