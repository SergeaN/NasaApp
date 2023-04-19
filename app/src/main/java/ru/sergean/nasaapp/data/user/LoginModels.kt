package ru.sergean.nasaapp.data.user

import com.google.gson.annotations.SerializedName

data class LoginReceive(
    @SerializedName("email") val email: String,
    val password: String,
)

data class LoginResponse(
    @SerializedName("token") val token: String? = null,
    val error: String? = null,
)
