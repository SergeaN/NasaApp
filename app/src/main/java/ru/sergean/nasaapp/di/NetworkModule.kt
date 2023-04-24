package ru.sergean.nasaapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.sergean.nasaapp.data.images.remote.ImagesRemoteDataSource
import ru.sergean.nasaapp.data.images.remote.MockImagesRemoteDataSource
import ru.sergean.nasaapp.data.images.remote.NasaService
import ru.sergean.nasaapp.data.network.NetworkConnectionManager
import ru.sergean.nasaapp.data.network.NetworkConnectionManagerImpl
import ru.sergean.nasaapp.data.user.MockUserService
import ru.sergean.nasaapp.data.user.UserService
import ru.sergean.nasaapp.data.user.USER_SERVICE_URL
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    fun provideNetworkConnectionManager(
        networkConnectionManager: NetworkConnectionManagerImpl
    ): NetworkConnectionManager {
        return networkConnectionManager
    }

    @Provides
    @Singleton
    fun provideImagesRemoteDataSource(nasaService: NasaService): ImagesRemoteDataSource {
        //return nasaService
        return MockImagesRemoteDataSource
    }

    @Provides
    @Singleton
    fun provideUserService(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): UserService {
/*
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(USER_SERVICE_URL)
            .addConverterFactory(converterFactory)
            .build()
            .create()
*/

        return MockUserService
    }


    @Provides
    @Singleton
    fun provideNasaService(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): NasaService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://images-api.nasa.gov")
            .addConverterFactory(converterFactory)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            //.addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptorInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

}