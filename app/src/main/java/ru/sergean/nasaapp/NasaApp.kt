package ru.sergean.nasaapp

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.sergean.nasaapp.di.app.DaggerAppComponent
import ru.sergean.nasaapp.di.app.AppComponent
import ru.sergean.nasaapp.di.app.FirebaseDependencies
import ru.sergean.nasaapp.di.login.LoginComponent

class NasaApp() : Application(), FirebaseDependencies {

    override val firebaseAuth: FirebaseAuth
        get() = Firebase.auth

    override val firebaseDatabase: FirebaseDatabase
        get() = Firebase.database

    val appComponent: AppComponent
        get() = checkNotNull(_appComponent) {
            "AppComponent isn't initialized"
        }

    private var _appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        _appComponent = DaggerAppComponent.builder()
            .applicationContext(context = this)
            .firebaseDependencies(this)
            .build()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is NasaApp -> appComponent
        else -> this.applicationContext.appComponent
    }
