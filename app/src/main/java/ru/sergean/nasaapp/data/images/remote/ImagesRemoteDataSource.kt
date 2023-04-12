package ru.sergean.nasaapp.data.images.remote

data class ImageRemoteModel(
    val title: String,
    val description: String,
    val dateCreated: String,
    val nasaId: String,
    val imageUrl: String,
)

interface ImagesRemoteDataSource {
    suspend fun fetchImages(query: String): NasaResponse
}

object MockImagesRemoteDataSource : ImagesRemoteDataSource {
    override suspend fun fetchImages(query: String): NasaResponse {
        return mockNasaResponse
    }
}
