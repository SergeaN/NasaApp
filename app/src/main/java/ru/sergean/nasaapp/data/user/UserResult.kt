package ru.sergean.nasaapp.data.user

sealed interface LoginResult {
    data class Success(val token: String): LoginResult
    data class Error(val exception: Exception): LoginResult
}