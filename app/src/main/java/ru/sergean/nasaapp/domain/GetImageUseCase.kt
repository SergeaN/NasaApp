package ru.sergean.nasaapp.domain

import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.ImageRepository
import javax.inject.Inject

class GetImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend fun invoke(nasaId: String): ImageModel? = imageRepository.getImage(nasaId)
}