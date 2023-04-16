package ru.sergean.nasaapp.presentation.ui.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.databinding.FragmentDetailBinding
import ru.sergean.nasaapp.utils.parcelableArgs
import ru.sergean.nasaapp.utils.showSnackbar
import javax.inject.Inject

class DetailFragment : Fragment(R.layout.fragment_detail) {

    companion object {
        const val ARG_IMAGE = "arg_image_for_detail"
    }

    private val image: ImageModel by parcelableArgs(ARG_IMAGE)

    private val binding by viewBinding(FragmentDetailBinding::bind)

    @Inject
    lateinit var viewModelFactory: DetailFactory.Factory

    private val viewModel: DetailViewModel by viewModels {
        viewModelFactory.create(image.nasaId)
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(fragment = this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: $image")

        observeState()
        observeSideEffects()

        binding.run {
            detailImage.load(data = image.imageUrl) {
                crossfade(enable = true)
                placeholder(R.drawable.item_image_placeholder)
                error(R.drawable.error)
                transformations(RoundedCornersTransformation())
            }

            favoriteButton.setOnClickListener { viewModel.dispatch(DetailAction.ChangeSaving) }
        }

    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.observeState().flowWithLifecycle(lifecycle).collect {
                Log.d(TAG, "observeState: $it")

                val stateImage = it.image

                binding.run {
                    favoriteButton.isEnabled = it.progress.not()
                    favoriteButton.setIcon(it.imageSaved)
                    favoriteButton.setText(it.imageSaved)

                    detailTitle.text = stateImage?.title
                    detailDescription.text = stateImage?.description
                    detailDate.text = stateImage?.dateCreated
                }
            }
        }
    }

    private fun observeSideEffects() {
        lifecycleScope.launch {
            viewModel.observeSideEffect().flowWithLifecycle(lifecycle).collect {
                Log.d(TAG, "observeSideEffects: $it")
                when (it) {
                    is DetailEffect.Message -> {
                        showSnackbar(it.text)
                    }
                }
            }
        }
    }

    private fun MaterialButton.setIcon(isSaved: Boolean) {
        val iconRes = when (isSaved) {
            true -> R.drawable.ic_favorites_filled
            false -> R.drawable.ic_favorites
        }
        icon = resources.getDrawable(iconRes)
    }

    private fun MaterialButton.setText(isSaved: Boolean) {
        val textRes = when (isSaved) {
            true -> R.string.remove_to_favorites
            false -> R.string.add_to_favorites
        }
        setText(textRes)
    }
}