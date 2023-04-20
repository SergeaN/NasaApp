package ru.sergean.nasaapp.data.images.remote

import android.util.Log
import ru.sergean.nasaapp.TAG

data class ImageRemoteModel(
    val title: String,
    val description: String,
    val dateCreated: String,
    val nasaId: String,
    val imageUrl: String,
)

fun NasaResponse.mapToImageRemote(): List<ImageRemoteModel> {
    return collection.items.mapNotNull {
        Log.d(TAG, "mapToImageRemote: $this")
        Log.d(TAG, "mapToImageRemote: $collection")
        Log.d(TAG, "mapToImageRemote: ${collection.items.size}")
        try {
            val imageRemote = it.data[0]
            val linkRemote = it.links[0]
            imageRemote.toItemWith(linkRemote)
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }
}

fun DataResponse.toItemWith(linkRemote: LinkRemote): ImageRemoteModel =
    ImageRemoteModel(title, description, dateCreated, nasaId, linkRemote.imageUrl)