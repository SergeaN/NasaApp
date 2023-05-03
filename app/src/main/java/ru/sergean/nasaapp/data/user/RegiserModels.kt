package ru.sergean.nasaapp.data.user

data class RegisterReceive(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
)

data class RegisterResponse(
    val token: String? = null,
    val error: String? = null,
)

data class RegisterInfo(
    val token: String
)

fun RegisterResponse.mapToInfo(): RegisterInfo = when {
    error != null -> throw Exception("Register error: $error")
    token == null -> throw Exception("Token error")
    else -> RegisterInfo(token)
}


