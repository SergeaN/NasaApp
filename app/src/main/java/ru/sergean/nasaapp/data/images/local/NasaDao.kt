package ru.sergean.nasaapp.data.images.local

import androidx.room.*

@Dao
interface NasaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAll(images: List<ImageLocalModel>)

    @Query("SELECT * FROM image WHERE favorite = :isFavorites")
    suspend fun getAll(isFavorites: Boolean = false): List<ImageLocalModel>

    @Query("SELECT * FROM image WHERE favorite = :isFavorites AND (title LIKE :query OR description LIKE :query)")
    suspend fun getAll(query: String, isFavorites: Boolean = false): List<ImageLocalModel>

    @Query("UPDATE image SET favorite = :isFavorite WHERE nasaId = :nasaId")
    suspend fun updateImage(nasaId: String, isFavorite: Boolean)

    @Query("SELECT * FROM image WHERE nasaId = :nasaId")
    suspend fun getImage(nasaId: String): ImageLocalModel?

    @Insert
    suspend fun insertImage(image: ImageLocalModel)

    @Delete
    suspend fun deleteImage(image: ImageLocalModel)
}