package ru.sergean.nasaapp.di.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Qualifier

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR)
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationContext

interface FirebaseDependencies {
    val firebaseAuth: FirebaseAuth
    val firebaseDatabase: FirebaseDatabase
}