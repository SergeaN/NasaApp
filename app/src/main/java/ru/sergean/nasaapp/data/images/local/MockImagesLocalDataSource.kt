package ru.sergean.nasaapp.data.images.local

import kotlinx.coroutines.delay
import ru.sergean.nasaapp.data.images.remote.mapToImageRemote
import ru.sergean.nasaapp.data.images.remote.mockNasaResponse

object MockImagesLocalDataSource : ImagesLocalDataSource {

    override suspend fun getImage(imageId: String): ImageLocalModel? {
        delay(timeMillis = 1000)
        return null
    }

    override suspend fun fetchAllImages(query: String): List<ImageLocalModel> {
        delay(timeMillis = 1000)
        return mockNasaResponse.mapToImageRemote().map { it.toImageLocalModel() }
    }

    override suspend fun fetchFavoriteImages(query: String): List<ImageLocalModel> {
        delay(timeMillis = 1000)
        return mockNasaResponse.mapToImageRemote().map { it.toImageLocalModel() }
    }

    override suspend fun saveImages(images: List<ImageLocalModel>) {
        delay(timeMillis = 1000)
    }

    override suspend fun addToFavorites(imageId: String) {
        delay(timeMillis = 1000)
    }

    override suspend fun removeFromFavorites(imageId: String) {
        delay(timeMillis = 1000)
    }
}