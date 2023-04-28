package ru.sergean.nasaapp.di.app

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.sergean.nasaapp.data.images.local.*

private const val DATABASE_NAME = "nasa_app_db"

@Module
class DatabaseModule {

    @Provides
    fun provideNasaDao(appDatabase: AppDatabase): NasaDao {
        return appDatabase.nasaDao()
    }

    @Provides
    @AppScope
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }
}