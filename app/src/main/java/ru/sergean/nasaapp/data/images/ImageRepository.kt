package ru.sergean.nasaapp.data.images

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.data.base.ResultWrapper
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
    suspend fun fetchImages(query: String): ResultWrapper<List<ImageModel>> {

        val localImages: List<ImageModel> = try {
            localDataSource.fetchAllImages(query).map { it.toImageModel() }
        } catch (e: Exception) {
            emptyList()
        }

        val remoteImages = try {
            remoteDataSource.fetchImages(query).mapToImageRemote().map { it.toImageModel() }
        } catch (e: Exception) {
            emptyList()
        }

        saveImages(remoteImages)

        val resultSet = buildSet {
            addAll(localImages)
            addAll(remoteImages)
        }.sortedBy { it.dateCreated }

        return ResultWrapper.Success(data = resultSet)
    }

    suspend fun fetchFavoriteImages(query: String): ResultWrapper<List<ImageModel>> {
        return try {
            val images = localDataSource.fetchFavoriteImages(query).map { it.toImageModel() }
            ResultWrapper.Success(data = images)
        } catch (e: Exception) {
            Log.e(TAG, "fetchFavoriteImages:", e)
            ResultWrapper.Failure(message = e.message ?: "Error")
        }
    }

    suspend fun addToFavorites(nasaId: String) {
        try {
            localDataSource.addToFavorites(nasaId)
        } catch (e: Exception) {
            Log.e(TAG, "addToFavorites:", e)
        }
    }

    suspend fun removeFromFavorites(nasaId: String) {
        try {
            localDataSource.removeFromFavorites(nasaId)
        } catch (e: Exception) {
            Log.e(TAG, "removeFromFavorites:", e)
        }
    }

    suspend fun getImage(nasaId: String): ResultWrapper<ImageModel> {
        return try {
            localDataSource.getImage(nasaId)?.toImageModel()?.let {
                ResultWrapper.Success(data = it)
            } ?: throw Exception("Image with id=$nasaId not found")
        } catch (e: Exception) {
            ResultWrapper.Failure(message = e.message ?: "Error")
        }
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