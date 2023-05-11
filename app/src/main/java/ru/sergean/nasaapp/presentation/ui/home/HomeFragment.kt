package ru.sergean.nasaapp.presentation.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.toImageModel
import ru.sergean.nasaapp.databinding.FragmentHomeBinding
import ru.sergean.nasaapp.presentation.ui.base.adapter.FingerprintAdapter
import ru.sergean.nasaapp.presentation.ui.base.adapter.Item
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModelFactory
import ru.sergean.nasaapp.presentation.ui.detail.DetailFragment
import ru.sergean.nasaapp.presentation.ui.home.items.*
import ru.sergean.nasaapp.utils.dp
import ru.sergean.nasaapp.utils.showSnackbar
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val items: MutableList<Item> = mutableListOf()
    private var fingerprintAdapter: FingerprintAdapter? = null

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        updateItems()

        viewModel.dispatch(HomeAction.Refresh(force = false))

        observeState()
        observeSideEffects()

        binding.imageSwipeContainer.setOnRefreshListener {
            viewModel.dispatch(HomeAction.Refresh(force = true))
        }
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
            viewModel.observeSideEffect().flowWithLifecycle(lifecycle).collect { effect ->
                when (effect) {
                    is HomeEffect.Message -> showSnackbar(effect.text)
                }
            }
        }
    }

    private fun initRecyclerView() {
        fingerprintAdapter = FingerprintAdapter(getFingerprints()).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        val customLayoutManager = GridLayoutManager(requireContext(), 3).apply {
            spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        0 -> 3
                        else -> 1
                    }
                }
            }
        }

        binding.imageRecyclerView.run {
            layoutManager = customLayoutManager
            adapter = fingerprintAdapter

            addItemDecoration(
                HomeSearchDecorator(horizontalDivider = 16.dp, verticalDivider = 8.dp)
            )

            addItemDecoration(
                HomeItemDecorator(innerHorizontalDivider = 2.dp, innerVerticalDivider = 2.dp)
            )
        }
    }

    private fun updateItems(images: List<ImageItem> = emptyList()) {
        items.removeAll { it !is SearchItem }
        if (items.count { it is SearchItem } == 0) {
            items.add(index = 0, SearchItem(initText = viewModel.observeState().value.query))
        }
        items.addAll(images)
        fingerprintAdapter?.submitList(items.toList())
    }

    private fun getFingerprints() = listOf(
        SearchItemFingerprint(lifecycleScope, ::onQueryChanged),
        ImageItemFingerprint(::onImageClick),
    )

    private fun onImageClick(imageItem: ImageItem) {
        navigateToDetailScreen(imageItem.toImageModel())
    }

    private fun navigateToDetailScreen(image: ImageModel) {
        val bundle = bundleOf(DetailFragment.ARG_IMAGE to image)
        findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
    }

    private fun onQueryChanged(query: String) {
        viewModel.dispatch(HomeAction.ChangeQuery(query))
    }

    companion object {
        const val ARG_TOKEN = "user_token"
    }
}