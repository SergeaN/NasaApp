package ru.sergean.nasaapp.domain

import ru.sergean.nasaapp.data.images.ImageModel
import ru.sergean.nasaapp.data.images.ImageRepository
import ru.sergean.nasaapp.presentation.ui.favorites.FavoriteImageItem
import javax.inject.Inject

class FetchFavoriteImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend fun invoke(query: String): List<ImageModel> = imageRepository.fetchFavoriteImages(query)
}