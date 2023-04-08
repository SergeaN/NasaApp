package ru.sergean.nasaapp.data.images.local

import kotlinx.coroutines.delay

data class ImageLocalModel(
    val title: String,
    val description: String,
    val dateCreated: String,
    val nasaId: String,
    val imageUrl: String,
)

interface ImagesLocalDataSource {
    suspend fun fetchAllImages(query: String): List<ImageLocalModel>
    suspend fun fetchFavoriteImages(query: String): List<ImageLocalModel>
    suspend fun fetchFavoriteImages(): List<ImageLocalModel>

    suspend fun saveImages(images: List<ImageLocalModel>)
    suspend fun saveImage(image: ImageLocalModel)
}

object MockImagesLocalDataSource : ImagesLocalDataSource {

    override suspend fun fetchAllImages(query: String): List<ImageLocalModel> {
        delay(timeMillis = 1000)
        return listOf()
    }

    override suspend fun fetchFavoriteImages(query: String): List<ImageLocalModel> {
        delay(timeMillis = 1000)
        return listOf()
    }

    override suspend fun fetchFavoriteImages(): List<ImageLocalModel> {
        delay(timeMillis = 1000)
        return listOf()
    }

    override suspend fun saveImages(images: List<ImageLocalModel>) {
        delay(timeMillis = 6000)
    }

    override suspend fun saveImage(image: ImageLocalModel) {
        delay(timeMillis = 3000)
    }
}
