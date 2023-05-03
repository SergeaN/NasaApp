package ru.sergean.nasaapp.presentation.ui.intro

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.parcelize.Parcelize
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.databinding.FragmentIntroItemBinding
import ru.sergean.nasaapp.utils.parcelableArgs
import ru.sergean.nasaapp.utils.serializableArgs
import java.io.Serializable

@Parcelize
data class IntroItemData(
    @DrawableRes val imageId: Int,
    @StringRes val textId: Int,
) : Parcelable

class IntroItemFragment : Fragment(R.layout.fragment_intro_item) {

    private val binding by viewBinding(FragmentIntroItemBinding::bind)
    private val introItemData: IntroItemData by parcelableArgs(ARG_DATA)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            introImage.setImageResource(introItemData.imageId)
            introText.text = getString(introItemData.textId)
        }
    }

    companion object {
        private const val ARG_DATA = "arg-data"

        fun newInstance(data: IntroItemData) = IntroItemFragment().apply {
            arguments = bundleOf(ARG_DATA to data)
        }
    }
}