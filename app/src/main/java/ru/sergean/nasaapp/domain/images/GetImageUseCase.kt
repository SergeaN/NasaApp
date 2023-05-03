package ru.sergean.nasaapp.domain.images

import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.ImageRepository
import javax.inject.Inject

class GetImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(nasaId: String): ResultWrapper<ImageModel> = imageRepository.getImage(nasaId)
}