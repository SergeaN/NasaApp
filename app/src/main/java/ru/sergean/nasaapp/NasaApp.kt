package ru.sergean.nasaapp

import android.app.Application
import android.content.Context
import ru.sergean.nasaapp.di.AppComponent

const val TAG = "debig"

class NasaApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

       // appComponent = DaggerAppComponent.builder().build()
    }

}

val Context.appComponent: AppComponent
    get() = when (this) {
        is NasaApp -> appComponent
        else -> this.applicationContext.appComponent
    }
