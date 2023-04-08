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
import ru.sergean.nasaapp.data.images.remote.NasaInterceptor
import ru.sergean.nasaapp.data.images.remote.NasaService
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideImagesRemoteDataSource(nasaService: NasaService): ImagesRemoteDataSource {
        //return nasaService
        return MockImagesRemoteDataSource
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
        interceptor: NasaInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            //.addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideNasaInterceptor(): NasaInterceptor {
        return NasaInterceptor()
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