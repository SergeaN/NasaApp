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
