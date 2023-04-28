package ru.sergean.nasaapp.di.login

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import ru.sergean.nasaapp.data.user.MockUserService
import ru.sergean.nasaapp.data.user.USER_SERVICE_URL
import ru.sergean.nasaapp.data.user.UserService

@Module
class LoginModule {

    @Provides
    @LoginScope
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

        return MockUserService()
    }
}