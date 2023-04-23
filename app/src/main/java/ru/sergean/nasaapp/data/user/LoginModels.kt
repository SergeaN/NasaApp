package ru.sergean.nasaapp.data.user

data class LoginReceive(
    val email: String,
    val password: String,
)

data class LoginResponse(
    val token: String? = null,
    val error: String? = null,
)

sealed interface LoginResult {
    data class Success(val token: String) : LoginResult
    data class Error(val exception: Exception) : LoginResult
}
