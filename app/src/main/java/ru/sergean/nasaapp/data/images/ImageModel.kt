package ru.sergean.nasaapp.data.images

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.sergean.nasaapp.data.images.local.ImageLocalModel
import ru.sergean.nasaapp.data.images.remote.ImageRemoteModel
import ru.sergean.nasaapp.presentation.ui.favorites.FavoriteImageItem
import ru.sergean.nasaapp.presentation.ui.home.ImageItem

@Parcelize
data class ImageModel(
    val title: String,
    val description: String,
    val dateCreated: String,
    val nasaId: String,
    val imageUrl: String,
) : Parcelable

fun ImageLocalModel.toImageModel() =
    ImageModel(title, description, dateCreated, nasaId, imageUrl)

fun ImageRemoteModel.toImageModel() =
    ImageModel(title, description, dateCreated, nasaId, imageUrl)

fun ImageItem.toImageModel() = ImageModel(
    title, description, dateCreated, nasaId, imageUrl
)
fun FavoriteImageItem.toImageModel() = ImageModel(
    title, description, dateCreated, nasaId, imageUrl
)