package ru.sergean.nasaapp.data.images.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ImageLocalModel::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nasaDao(): NasaDao
}