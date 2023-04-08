package ru.sergean.nasaapp.di

import dagger.Module
import dagger.Provides
import ru.sergean.nasaapp.data.images.local.ImagesLocalDataSource
import ru.sergean.nasaapp.data.images.local.MockImagesLocalDataSource
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideImagesLocalDataSource(): ImagesLocalDataSource {
        return MockImagesLocalDataSource
    }

}