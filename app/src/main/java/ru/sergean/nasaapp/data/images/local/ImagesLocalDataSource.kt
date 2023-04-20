package ru.sergean.nasaapp.data.images.local

import javax.inject.Inject

interface ImagesLocalDataSource {

    suspend fun fetchAllImages(query: String): List<ImageLocalModel>
    suspend fun fetchFavoriteImages(query: String): List<ImageLocalModel>
    suspend fun saveImages(images: List<ImageLocalModel>)

    suspend fun getImage(imageId: String): ImageLocalModel?
    suspend fun addToFavorites(imageId: String)
    suspend fun removeFromFavorites(imageId: String)
}

class ImageLocalDataSourceImpl @Inject constructor(
    private val nasaDao: NasaDao
) : ImagesLocalDataSource {

    override suspend fun fetchAllImages(query: String) = fetchImages(query, isFavorites = false)

    override suspend fun fetchFavoriteImages(query: String) = fetchImages(query, isFavorites = true)

    override suspend fun saveImages(images: List<ImageLocalModel>) = nasaDao.addAll(images)

    override suspend fun getImage(imageId: String) = nasaDao.getImageById(imageId)

    override suspend fun addToFavorites(imageId: String) {
        nasaDao.updateImageById(nasaId = imageId, isFavorite = true)
    }

    override suspend fun removeFromFavorites(imageId: String) {
        nasaDao.updateImageById(nasaId = imageId, isFavorite = false)
    }

    private suspend fun fetchImages(query: String, isFavorites: Boolean): List<ImageLocalModel> {
        return if (query.isNotEmpty() && query.isNotBlank()) {
            nasaDao.getAllByQuery(query, isFavorites)
        } else {
            nasaDao.getAll(isFavorites)
        }
    }
}