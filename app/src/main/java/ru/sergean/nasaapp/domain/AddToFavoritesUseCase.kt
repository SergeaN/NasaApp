package ru.sergean.nasaapp.domain

import ru.sergean.nasaapp.data.images.ImageRepository
import javax.inject.Inject

class AddToFavoritesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(nasaId: Int) = imageRepository.addToFavorites(nasaId)
}