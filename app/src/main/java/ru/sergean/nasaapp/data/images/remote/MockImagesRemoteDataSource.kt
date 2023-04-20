package ru.sergean.nasaapp.data.images.remote

object MockImagesRemoteDataSource : ImagesRemoteDataSource {

    override suspend fun fetchImages(query: String): NasaResponse {
        return mockNasaResponse
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
    add(mockItemsResponse0)
    add(mockItemsResponse1)
    add(mockItemsResponse2)
}

val mockNasaResponse = NasaResponse(
    collection = CollectionResponse(queryHref = "", items = mockItems)
)


