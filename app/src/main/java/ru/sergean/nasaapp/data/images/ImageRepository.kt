package ru.sergean.nasaapp.data.images

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.data.images.local.ImagesLocalDataSource
import ru.sergean.nasaapp.data.images.local.toImageLocalModel
import ru.sergean.nasaapp.data.images.remote.mapToImageRemote
import ru.sergean.nasaapp.data.images.remote.ImagesRemoteDataSource
import ru.sergean.nasaapp.di.app.AppScope
import javax.inject.Inject

@AppScope
class ImageRepository @Inject constructor(
    private val remoteDataSource: ImagesRemoteDataSource,
    private val localDataSource: ImagesLocalDataSource,
) {
    suspend fun fetchImages(query: String): List<ImageModel> {
        Log.d(TAG, "fetchImages: $query")

        val localImages: List<ImageModel> =
            localDataSource.fetchAllImages(query).map { it.toImageModel() }

        val remoteImages =
            remoteDataSource.fetchImages(query).mapToImageRemote().map { it.toImageModel() }

        saveImages(remoteImages)

        Log.d(TAG, "Local: ${localImages.size}")
        Log.d(TAG, "Remote: ${remoteImages.size}")

        val resultSet = buildSet {
            addAll(localImages)
            addAll(remoteImages)
        }.sortedBy { it.dateCreated }

        Log.d(TAG, "Result: ${resultSet.size}")
        return resultSet.toList()
    }

    suspend fun fetchFavoriteImages(query: String): List<ImageModel> {
        val localImages = localDataSource.fetchFavoriteImages(query)
        return localImages.map { it.toImageModel() }
    }

    suspend fun addToFavorites(nasaId: String) {
        localDataSource.addToFavorites(nasaId)
    }

    suspend fun removeFromFavorites(nasaId: String) {
        localDataSource.removeFromFavorites(nasaId)
    }

    suspend fun getImage(nasaId: String): ImageModel? {
        return localDataSource.getImage(nasaId)?.toImageModel()
    }

    private suspend fun saveImages(images: List<ImageModel>) =
        withContext(Dispatchers.IO) {
            launch {
                try {
                    localDataSource.saveImages(images.map { it.toImageLocalModel() })
                    Log.d(TAG, "saveImages: ")
                } catch (e: Exception) {
                    Log.e(TAG, "saveImages:", e)
                }
            }
        }
}