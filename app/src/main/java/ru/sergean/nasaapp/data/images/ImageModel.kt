package ru.sergean.nasaapp.data.images

import ru.sergean.nasaapp.data.images.local.ImageLocalModel
import ru.sergean.nasaapp.data.images.remote.ImageRemoteModel

data class ImageModel(
    val title: String,
    val description: String,
    val dateCreated: String,
    val nasaId: String,
    val imageUrl: String,
)

fun ImageLocalModel.toImageModel() =
    ImageModel(title, description, dateCreated, nasaId, imageUrl)

fun ImageRemoteModel.toImageModel() =
    ImageModel(title, description, dateCreated, nasaId, imageUrl)

fun ImageRemoteModel.toImageLocalModel() =
    ImageLocalModel(title, description, dateCreated, nasaId, imageUrl)

fun ImageModel.toImageLocalModel() =
    ImageLocalModel(title, description, dateCreated, nasaId, imageUrl)
