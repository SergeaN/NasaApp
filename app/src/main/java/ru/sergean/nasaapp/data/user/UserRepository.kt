package ru.sergean.nasaapp.data.user

import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) {

    suspend fun login(email: String, password: String): LoginResult {
        val receive = LoginReceive(email, password)
        val response = userService.login(receive)
        return when {
            response.error != null -> LoginResult.Error(Exception(response.error))
            response.token != null -> LoginResult.Success(token = response.token)
            else -> LoginResult.Error(Exception("Token Error"))
        }
    }
}