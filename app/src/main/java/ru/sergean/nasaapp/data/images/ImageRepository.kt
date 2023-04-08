package ru.sergean.nasaapp.data.images

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.data.images.local.ImagesLocalDataSource
import ru.sergean.nasaapp.data.images.remote.ImageRemoteModel
import ru.sergean.nasaapp.data.images.remote.mapToImageRemote
import ru.sergean.nasaapp.data.images.remote.ImagesRemoteDataSource
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val remoteDataSource: ImagesRemoteDataSource,
    private val localDataSource: ImagesLocalDataSource,
) {
    suspend fun fetchImages(query: String): List<ImageModel> {
        val localImages =
            localDataSource.fetchAllImages(query).map { it.toImageModel() }
        val remoteImages =
            remoteDataSource.fetchImages(query).mapToImageRemote().map { it.toImageModel() }

        saveImages(remoteImages)

        return buildSet {
            addAll(localImages)
            addAll(remoteImages)
        }.toList()
    }

    suspend fun fetchFavoriteImages(query: String): List<ImageModel> {
        val localImages = if (query.isBlank() || query.isEmpty()) {
            localDataSource.fetchFavoriteImages()
        } else {
            localDataSource.fetchFavoriteImages(query)
        }
        return localImages.map { it.toImageModel() }
    }


    private suspend fun saveImages(images: List<ImageModel>) =
        withContext(Dispatchers.IO) {
            launch {
                try {
                    localDataSource.saveImages(images.map { it.toImageLocalModel() })
                } catch (e: Exception) {
                    Log.e(TAG, "saveImages:", e)
                }
            }
        }
}