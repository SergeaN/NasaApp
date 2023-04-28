package ru.sergean.nasaapp.di.app

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
import ru.sergean.nasaapp.data.images.remote.NasaService

@Module
class NetworkModule {

    @Provides
    @AppScope
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
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    fun provideHttpLoggingInterceptorInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun provideGsonConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

}