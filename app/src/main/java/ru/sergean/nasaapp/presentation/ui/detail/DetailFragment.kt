package ru.sergean.nasaapp.presentation.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import coil.transform.RoundedCornersTransformation
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.databinding.FragmentDetailBinding
import ru.sergean.nasaapp.utils.parcelableArgs

class DetailFragment : Fragment(R.layout.fragment_detail) {

    companion object {
        const val ARG_IMAGE = "arg_image_for_detail"
    }

    private val image: ImageModel by parcelableArgs(ARG_IMAGE)
    private val binding by viewBinding(FragmentDetailBinding::bind)

    private val viewModel: DetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            detailTitle.text = image.title
            detailDescription.text = image.description
            detailDate.text = image.dateCreated

            detailImage.load(data = image.imageUrl) {
                crossfade(enable = true)
                placeholder(R.drawable.item_image_placeholder)
                error(R.drawable.error)
                transformations(RoundedCornersTransformation())
            }
        }
    }
}