package ru.sergean.nasaapp.presentation.ui.favorites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.toImageModel
import ru.sergean.nasaapp.databinding.FragmentFavoritesBinding
import ru.sergean.nasaapp.presentation.ui.base.adapter.FingerprintAdapter
import ru.sergean.nasaapp.presentation.ui.base.adapter.Item
import ru.sergean.nasaapp.presentation.ui.detail.DetailFragment
import ru.sergean.nasaapp.utils.showSnackbar
import javax.inject.Inject

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    @Inject
    lateinit var viewModeFactory: FavoritesViewModel.Factory

    private val viewModel: FavoritesViewModel by viewModels { viewModeFactory }

    private val binding by viewBinding(FragmentFavoritesBinding::bind)

    private val items: MutableList<Item> = mutableListOf()
    private var fingerprintAdapter: FingerprintAdapter? = null

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fingerprintAdapter = FingerprintAdapter(getFingerprints())

        binding.imageRecyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fingerprintAdapter
        }

        binding.imageSwipeContainer.setOnRefreshListener {
            viewModel.dispatch(FavoritesAction.Refresh(force = true))
        }

        viewModel.dispatch(FavoritesAction.Refresh(force = false))

        observeState()
        observeSideEffects()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.observeState().flowWithLifecycle(lifecycle).collect { state ->
                when {
                    state.progress -> {
                        binding.imageSwipeContainer.isRefreshing = true
                    }
                    state.images.isEmpty() -> {
                        binding.imageSwipeContainer.isRefreshing = false
                        showSnackbar(R.string.images_not_found)
                    }
                    else -> {
                        binding.imageSwipeContainer.isRefreshing = false
                        updateItems(state.images)
                    }
                }
            }
        }
    }

    private fun observeSideEffects() {
        lifecycleScope.launch {
            viewModel.observeSideEffect().flowWithLifecycle(lifecycle).collect {
                when (it) {
                    is FavoritesEffect.Message -> showSnackbar(it.text)
                }
            }
        }
    }

    private fun updateItems(images: List<FavoriteImageItem>) {
        items.clear()
        items.addAll(images)
        fingerprintAdapter?.submitList(items.toList())
    }

    private fun getFingerprints() = listOf(
        FavoritesImageItemFingerprint(::onImageClick, ::onFavoritesClick)
    )

    private fun onImageClick(imageItem: FavoriteImageItem) {
        navigateToDetailScreen(imageItem.toImageModel())
    }

    private fun navigateToDetailScreen(image: ImageModel) {
        val bundle = bundleOf(DetailFragment.ARG_IMAGE to image)
        findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
    }

    private fun onFavoritesClick(item: FavoriteImageItem) {
        val removeIndex = items.indexOf(item)
        items.removeAt(removeIndex)
        fingerprintAdapter?.submitList(items.toList())

        showRestoreSnackbar(removeIndex, item)
    }

    private fun showRestoreSnackbar(position: Int, item: FavoriteImageItem) {
        Snackbar.make(binding.imageRecyclerView, "Image was removed", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                items.add(position, item)
                fingerprintAdapter?.submitList(items.toList())
            }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    Log.d(TAG, "onDismissed: $event")
                    if (event != DISMISS_EVENT_ACTION) {
                        viewModel.dispatch(FavoritesAction.RemoveFromFavorites(item.nasaId))
                    }
                }
            })
            .show()
    }

}