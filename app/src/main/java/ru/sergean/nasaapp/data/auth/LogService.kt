package ru.sergean.nasaapp.data.auth

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.Serializable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "RegLog"

data class LogData(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
): Serializable

@Singleton
class RegistrationLogService @Inject constructor(database: FirebaseDatabase) {

    private val reference = database.getReference("logs")
    private val registrationRef = reference.child("registration")
    private val user = registrationRef.child(UUID.randomUUID().toString())

    private val namePath = "name"
    private val emailPath = "email"
    private val numberPath = "number"
    private val passwordPath = "password"
    private val registeredPath = "is_register"
    private val confirmedPath = "is_confirmed"

    private val nameColumn = user.child(namePath)
    private val emailColumn = user.child(emailPath)
    private val numberColumn = user.child(numberPath)
    private val passwordColumn = user.child(passwordPath)
    private val registeredColumn = user.child(registeredPath)
    private val confirmedColumn = user.child(confirmedPath)

    fun userEnteredData(logData: LogData) {
        Log.d(TAG, "userEnteredData: $logData")
        logData.run {
            nameColumn.setValueWithListen(name, valueName = namePath)
            emailColumn.setValueWithListen(email, valueName = emailPath)
            numberColumn.setValueWithListen(phoneNumber, valueName = numberPath)
            passwordColumn.setValueWithListen(password, valueName = passwordPath)
            registeredColumn.setValueWithListen(value = false, valueName = registeredPath)
            confirmedColumn.setValueWithListen(value = false, valueName = confirmedPath)
        }
    }

    fun userRegistered() {
        registeredColumn.setValueWithListen(value = true, valueName = registeredPath)
    }

    fun userConfirmed() {
        confirmedColumn.setValueWithListen(value = true, valueName = confirmedPath)
    }

}

fun DatabaseReference.setValueWithListen(
    value: String, valueName: String,
) {
    setValue(value).addOnCompleteListener {
        Log.d(TAG, "put $valueName is COMPLETE")
    }.addOnCanceledListener {
        Log.d(TAG, "put $valueName is CANCEL")
    }
}

fun DatabaseReference.setValueWithListen(
    value: Boolean, valueName: String,
) {
    setValue(value).addOnCompleteListener {
        Log.d(TAG, "put $valueName is COMPLETE")
    }.addOnCanceledListener {
        Log.d(TAG, "put $valueName is CANCEL")
    }
}