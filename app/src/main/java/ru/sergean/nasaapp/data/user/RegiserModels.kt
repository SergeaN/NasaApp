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

sealed interface RegisterResult {
    data class Success(val token: String) : RegisterResult
    data class Error(val exception: Exception) : RegisterResult
}

