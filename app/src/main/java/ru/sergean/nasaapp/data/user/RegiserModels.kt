package ru.sergean.nasaapp.data.user

import java.io.Serializable

data class RegisterReceive(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
)

data class RegisterResponse(
    val token: String? = null,
    val error: String? = null,
)

fun RegistrationData.mapToReceive() = RegisterReceive(name, email, password, phoneNumber)

data class RegistrationData(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
): Serializable