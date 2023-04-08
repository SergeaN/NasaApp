package ru.sergean.nasaapp.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.squareup.picasso.Picasso
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.databinding.ItemImageBinding
import ru.sergean.nasaapp.presentation.ui.base.adapter.BaseViewHolder
import ru.sergean.nasaapp.presentation.ui.base.adapter.Item
import ru.sergean.nasaapp.presentation.ui.base.adapter.ItemFingerprint

fun ImageModel.mapToImageItem(): ImageItem {
    return ImageItem(title, description, dateCreated, nasaId, imageUrl, isSaved = false)
}

data class ImageItem(
    val title: String,
    val description: String,
    val dateCreated: String,
    val nasaId: String,
    val imageUrl: String,
    val isSaved: Boolean,
) : Item

class ImageItemFingerprint(
    private val onImageClick: (ImageItem) -> Unit,
) : ItemFingerprint<ItemImageBinding, ImageItem> {

    override fun isRelativeItem(item: Item) = item is ImageItem

    override fun getLayoutId() = R.layout.item_favorites_image

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemImageBinding, ImageItem> {
        val binding = ItemImageBinding.inflate(layoutInflater, parent, false)
        return ImageViewHolder(binding, onImageClick)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<ImageItem>() {

        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem.nasaId == newItem.nasaId
        }

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: ImageItem, newItem: ImageItem): Any? {
            if (oldItem.isSaved != newItem.isSaved) return newItem.isSaved
            return super.getChangePayload(oldItem, newItem)
        }
    }
}

class ImageViewHolder(
    binding: ItemImageBinding,
    private val onImageClick: (ImageItem) -> Unit,
) : BaseViewHolder<ItemImageBinding, ImageItem>(binding) {

    init {
        binding.root.setOnClickListener { onImageClick(item) }
    }

    override fun onBind(item: ImageItem) {
        super.onBind(item)
        Picasso.get()
            .load(item.imageUrl)
            .placeholder(R.drawable.item_image_placeholder)
            .error(R.drawable.error)
            .into(binding.itemImage)
    }
}