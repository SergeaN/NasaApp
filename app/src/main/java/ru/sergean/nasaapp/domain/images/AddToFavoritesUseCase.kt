package ru.sergean.nasaapp.domain.images

import ru.sergean.nasaapp.data.images.ImageRepository
import javax.inject.Inject

class AddToFavoritesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(nasaId: String) = imageRepository.addToFavorites(nasaId)
}