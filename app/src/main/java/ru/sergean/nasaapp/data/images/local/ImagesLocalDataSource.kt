package ru.sergean.nasaapp.data.images.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.delay
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.remote.ImageRemoteModel
import ru.sergean.nasaapp.data.images.remote.mapToImageRemote
import ru.sergean.nasaapp.data.images.remote.mockNasaResponse
import javax.inject.Inject

@Entity(tableName = "image")
data class ImageLocalModel(
    val title: String,
    val description: String,
    @ColumnInfo(name = "date_created") val dateCreated: String,
    @PrimaryKey val nasaId: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "favorite") val isFavorite: Boolean = false,
)

fun ImageRemoteModel.toImageLocalModel(): ImageLocalModel =
    ImageLocalModel(title, description, dateCreated, nasaId, imageUrl)

fun ImageModel.toImageLocalModel(): ImageLocalModel =
    ImageLocalModel(title, description, dateCreated, nasaId, imageUrl)

interface ImagesLocalDataSource {

    suspend fun fetchAllImages(query: String): List<ImageLocalModel>
    suspend fun fetchFavoriteImages(query: String): List<ImageLocalModel>

    suspend fun saveImages(images: List<ImageLocalModel>)

    suspend fun addToFavorites(imageId: Int)
    suspend fun removeFromFavorites(imageId: Int)
}

class ImageLocalDataSourceImpl @Inject constructor(
    private val nasaDao: NasaDao
) : ImagesLocalDataSource {

    override suspend fun fetchAllImages(query: String) = fetchImages(query, isFavorites = false)

    override suspend fun fetchFavoriteImages(query: String) = fetchImages(query, isFavorites = true)

    private suspend fun fetchImages(query: String, isFavorites: Boolean): List<ImageLocalModel> {
        return if (query.isNotEmpty() && query.isNotBlank()) {
            nasaDao.getAll(query, isFavorites)
        } else {
            nasaDao.getAll(isFavorites)
        }
    }

    override suspend fun saveImages(images: List<ImageLocalModel>) = nasaDao.addAll(images)

    override suspend fun addToFavorites(imageId: Int) {
        nasaDao.updateImage(nasaId = imageId, isFavorite = true)
    }

    override suspend fun removeFromFavorites(imageId: Int) {
        nasaDao.updateImage(nasaId = imageId, isFavorite = false)
    }
}

object MockImagesLocalDataSource : ImagesLocalDataSource {

    override suspend fun fetchAllImages(query: String): List<ImageLocalModel> {
        //delay(timeMillis = 2000)
        return mockNasaResponse.mapToImageRemote().map { it.toImageLocalModel() }
    }

    override suspend fun fetchFavoriteImages(query: String): List<ImageLocalModel> {
        delay(timeMillis = 1000)
        return mockNasaResponse.mapToImageRemote().map { it.toImageLocalModel() }
    }

    override suspend fun saveImages(images: List<ImageLocalModel>) {
        delay(timeMillis = 6000)
    }

    override suspend fun addToFavorites(imageId: Int) {
        delay(timeMillis = 1000)
    }

    override suspend fun removeFromFavorites(imageId: Int) {
        delay(timeMillis = 1000)
    }
}
