package ru.sergean.nasaapp.data.user

data class LoginReceive(
    val email: String,
    val password: String,
)

data class LoginResponse(
    val token: String? = null,
    val error: String? = null,
)

data class LoginInfo(
    val token: String
)

fun LoginResponse.mapToInfo(): LoginInfo = when {
    error != null -> throw Exception("Login error: $error")
    token == null -> throw Exception("Token error")
    else -> LoginInfo(token)
}
