package ru.sergean.nasaapp.data.images.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NasaService : ImagesRemoteDataSource {

    @GET("/search?media_type=image")
    override suspend fun fetchImages(@Query("q") query: String): NasaResponse

    @GET("/search?media_type=image")
    fun searchByYear(
        @Query("q") query: String,
        @Query("year_start") startYear: String,
        @Query("year_end") startEnd: String
    ): NasaResponse
}



