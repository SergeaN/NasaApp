package ru.sergean.nasaapp.data.images.remote

interface ImagesRemoteDataSource {
    suspend fun fetchImages(query: String): NasaResponse
}
