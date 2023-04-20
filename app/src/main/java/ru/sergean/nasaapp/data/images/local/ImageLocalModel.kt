package ru.sergean.nasaapp.data.images.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.remote.ImageRemoteModel

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
    ImageLocalModel(title, description, dateCreated, nasaId, imageUrl, isFavorite)