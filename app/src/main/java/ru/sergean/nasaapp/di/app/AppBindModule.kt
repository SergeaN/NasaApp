package ru.sergean.nasaapp.di.app

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.sergean.nasaapp.data.images.local.ImageLocalDataSourceImpl
import ru.sergean.nasaapp.data.images.local.ImagesLocalDataSource
import ru.sergean.nasaapp.data.images.remote.ImagesRemoteDataSource
import ru.sergean.nasaapp.data.images.remote.MockImagesRemoteDataSource
import ru.sergean.nasaapp.data.images.remote.NasaService
import ru.sergean.nasaapp.data.network.NetworkConnectionManager
import ru.sergean.nasaapp.data.network.NetworkConnectionManagerImpl
import javax.inject.Singleton

@Module
interface AppBindModule {

    @Binds
    fun bindImagesRemoteDataSource(dataSource: MockImagesRemoteDataSource): ImagesRemoteDataSource

    @Binds
    fun bindImagesLocalDataSource(dataSource: ImageLocalDataSourceImpl): ImagesLocalDataSource

    @Binds
    fun bindNetworkConnectionManager(
        networkConnectionManager: NetworkConnectionManagerImpl
    ): NetworkConnectionManager
}