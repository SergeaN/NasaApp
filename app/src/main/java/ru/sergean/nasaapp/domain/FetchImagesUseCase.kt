package ru.sergean.nasaapp.domain

import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.ImageRepository
import javax.inject.Inject

class FetchImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend fun invoke(query: String): List<ImageModel> = imageRepository.fetchImages(query)
}