package ru.sergean.nasaapp.domain.images

import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.ImageRepository
import javax.inject.Inject

class FetchImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(query: String): ResultWrapper<List<ImageModel>> =
        imageRepository.fetchImages(query)
}