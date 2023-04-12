package ru.sergean.nasaapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.sergean.nasaapp.data.images.local.*
import javax.inject.Singleton

private const val DATABASE_NAME = "nasa_app_db"

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideImagesLocalDataSource(dataSource: ImageLocalDataSourceImpl): ImagesLocalDataSource {
        return MockImagesLocalDataSource
        //return dataSource
    }

    @Provides
    @Singleton
    fun provideNasaDao(appDatabase: AppDatabase): NasaDao {
        return appDatabase.nasaDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }
}