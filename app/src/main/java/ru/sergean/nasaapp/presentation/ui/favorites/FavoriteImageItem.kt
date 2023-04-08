package ru.sergean.nasaapp.presentation.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.databinding.ItemFavoritesImageBinding
import ru.sergean.nasaapp.presentation.ui.base.adapter.BaseViewHolder
import ru.sergean.nasaapp.presentation.ui.base.adapter.Item
import ru.sergean.nasaapp.presentation.ui.base.adapter.ItemFingerprint

fun ImageModel.mapFavoriteImageItem(): FavoriteImageItem {
    return FavoriteImageItem(title, description, dateCreated, nasaId, imageUrl, isSaved = false)
}

data class FavoriteImageItem(
    val title: String,
    val description: String,
    val dateCreated: String,
    val nasaId: String,
    val imageUrl: String,
    val isSaved: Boolean,
) : Item

class FavoritesImageItemFingerprint(
    private val onImageClick: (FavoriteImageItem) -> Unit,
    private val onSaveImage: (FavoriteImageItem) -> Unit
) : ItemFingerprint<ItemFavoritesImageBinding, FavoriteImageItem> {

    override fun isRelativeItem(item: Item) = item is FavoriteImageItem

    override fun getLayoutId() = R.layout.item_favorites_image

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemFavoritesImageBinding, FavoriteImageItem> {
        val binding = ItemFavoritesImageBinding.inflate(layoutInflater, parent, false)
        return FavoritesImageViewHolder(binding, onImageClick, onSaveImage)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<FavoriteImageItem>() {

        override fun areItemsTheSame(
            oldItem: FavoriteImageItem,
            newItem: FavoriteImageItem
        ): Boolean {
            return oldItem.nasaId == newItem.nasaId
        }

        override fun areContentsTheSame(
            oldItem: FavoriteImageItem,
            newItem: FavoriteImageItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(
            oldItem: FavoriteImageItem,
            newItem: FavoriteImageItem
        ): Any? {
            if (oldItem.isSaved != newItem.isSaved) return newItem.isSaved
            return super.getChangePayload(oldItem, newItem)
        }
    }
}

class FavoritesImageViewHolder(
    binding: ItemFavoritesImageBinding,
    private val onImageClick: (FavoriteImageItem) -> Unit,
    private val onSaveImage: (FavoriteImageItem) -> Unit
) : BaseViewHolder<ItemFavoritesImageBinding, FavoriteImageItem>(binding) {

    init {
        binding.run {
            root.setOnClickListener {
                onImageClick(item)
            }

            imageSaveButton.setOnClickListener {
                if (bindingAdapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                onSaveImage(item)
            }
        }
    }

    override fun onBind(item: FavoriteImageItem) {
        super.onBind(item)
        binding.run {
            itemImageTitle.text = item.title

            Picasso.get()
                .load(item.imageUrl)
                .placeholder(R.drawable.item_image_placeholder)
                .error(R.drawable.error)
                .into(itemFavoritesImage)
        }
    }

    override fun onBind(item: FavoriteImageItem, payloads: List<Any>) {
        super.onBind(item, payloads)
        val isSaved = payloads.last() as Boolean
        binding.imageSaveButton.setChecked(isSaved)
    }

    private fun ImageView.setChecked(isChecked: Boolean) {
        val icon = when (isChecked) {
            true -> R.drawable.ic_bookmark_fill_24
            false -> R.drawable.ic_bookmark_border_24
        }
        setImageResource(icon)
    }
}