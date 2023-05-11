package ru.sergean.nasaapp.data.auth

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ru.sergean.nasaapp.di.app.AppScope
import java.util.*
import javax.inject.Inject

private const val TAG = "RegLog"

@AppScope
class RegistrationLogService @Inject constructor(database: FirebaseDatabase) {

    private val reference = database.getReference("logs")
    private val registrationRef = reference.child("registration")
    private val user = registrationRef.child(UUID.randomUUID().toString())

    private val namePath = "name"
    private val emailPath = "email"
    private val numberPath = "number"
    private val passwordPath = "password"
    private val confirmedPath = "is_confirmed"
    private val registeredPath = "is_register"

    private val nameColumn = user.child(namePath)
    private val emailColumn = user.child(emailPath)
    private val numberColumn = user.child(numberPath)
    private val passwordColumn = user.child(passwordPath)
    private val confirmedColumn = user.child(confirmedPath)
    private val registeredColumn = user.child(registeredPath)

    fun userEnteredData(name: String, email: String, phoneNumber: String, password: String) {
        Log.d(TAG, "userEnteredData: $name $email $phoneNumber  $password")
        nameColumn.setValueWithListen(name, valueName = namePath)
        emailColumn.setValueWithListen(email, valueName = emailPath)
        passwordColumn.setValueWithListen(password, valueName = passwordPath)
        numberColumn.setValueWithListen(phoneNumber, valueName = numberPath)
        confirmedColumn.setValueWithListen(value = false, valueName = confirmedPath)
        registeredColumn.setValueWithListen(value = false, valueName = registeredPath)
    }

    fun userConfirmed() {
        confirmedColumn.setValueWithListen(value = true, valueName = confirmedPath)
    }

    fun userRegistered() {
        registeredColumn.setValueWithListen(value = true, valueName = registeredPath)
    }
}

private fun DatabaseReference.setValueWithListen(
    value: String, valueName: String,
) {
    setValue(value).addOnCompleteListener {
        //Log.d(TAG, "put $valueName is COMPLETE")
    }.addOnCanceledListener {
        //Log.d(TAG, "put $valueName is CANCEL")
    }
}

private fun DatabaseReference.setValueWithListen(
    value: Boolean, valueName: String,
) {
    setValue(value).addOnCompleteListener {
        //Log.d(TAG, "put $valueName is COMPLETE")
    }.addOnCanceledListener {
        //Log.d(TAG, "put $valueName is CANCEL")
    }
}