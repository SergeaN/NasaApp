package ru.sergean.nasaapp.data.images.remote

import com.google.gson.annotations.SerializedName

data class NasaResponse(
    @SerializedName("collection") val collection: CollectionResponse,
)

data class CollectionResponse(
    @SerializedName("href") val queryHref: String,
    @SerializedName("items") val items: List<ItemsResponse>
)

data class ItemsResponse(
    @SerializedName("data") val data: List<DataResponse>,
    @SerializedName("links") val links: List<LinkRemote>,
)

data class DataResponse(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("date_created") val dateCreated: String,
    @SerializedName("nasa_id") val nasaId: String,
)

data class LinkRemote(
    @SerializedName("href") val imageUrl: String,
)

fun DataResponse.toItemWith(linkRemote: LinkRemote): ImageRemoteModel {
    return ImageRemoteModel(title, description, dateCreated, nasaId, linkRemote.imageUrl)
}

fun NasaResponse.mapToImageRemote(): List<ImageRemoteModel> {
    return collection.items.mapNotNull {
        try {
            val imageRemote = it.data[0]
            val linkRemote = it.links[0]
            imageRemote.toItemWith(linkRemote)
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }
}


