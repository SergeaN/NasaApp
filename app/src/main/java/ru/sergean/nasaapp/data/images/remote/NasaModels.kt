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


private val mockItemsResponse0 = ItemsResponse(
    data = listOf(
        DataResponse(
            title = "Nearside of the Moon",
            description = "Nearside of the Moon",
            dateCreated = "2009-09-24T18:00:22Z",
            nasaId = "PIA12235",
        )
    ),
    links = listOf(LinkRemote(imageUrl = "https://images-assets.nasa.gov/image/PIA12235/PIA12235~thumb.jpg"))
)

private val mockItemsResponse1 = ItemsResponse(
    data = listOf(
        DataResponse(
            title = "Mapping the Moon, Point by Point",
            description = "Mapping the Moon, Point by Point",
            dateCreated = "2009-09-24T18:00:20Z",
            nasaId = "PIA12233",
        )
    ),
    links = listOf(LinkRemote(imageUrl = "https://images-assets.nasa.gov/image/PIA12233/PIA12233~thumb.jpg"))
)

private val mockItemsResponse2 = ItemsResponse(
    data = listOf(
        DataResponse(
            title = "The Moon Largest Impact Basin",
            description = "The Moon Largest Impact Basin",
            dateCreated = "2010-07-13T22:24:40Z",
            nasaId = "PIA13496",
        )
    ),
    links = listOf(LinkRemote(imageUrl = "https://images-assets.nasa.gov/image/PIA13496/PIA13496~thumb.jpg"))
)

private val mockItems = buildList {
    repeat(times = 15){
        add(mockItemsResponse0)
        add(mockItemsResponse1)
        add(mockItemsResponse2)
    }
}

val mockNasaResponse = NasaResponse(
    collection = CollectionResponse(
        queryHref = "",
        items = mockItems
    )
)


