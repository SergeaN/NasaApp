package ru.sergean.nasaapp.data.user

import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) {

    suspend fun login(
        email: String, password: String
    ): LoginResult {
        val receiveData = LoginReceive(email, password)
        val response = userService.login(receiveData)
        return when {
            response.error != null -> LoginResult.Error(Exception(response.error))
            response.token != null -> LoginResult.Success(token = response.token)
            else -> LoginResult.Error(Exception("Token Error"))
        }
    }

    suspend fun register(
        name: String, email: String, phoneNumber: String, password: String
    ): RegisterResult {
        val receiveData = RegisterReceive(name, email, password, phoneNumber)
        val response = userService.register(receiveData)
        return when {
            response.error != null -> RegisterResult.Error(Exception(response.error))
            response.token != null -> RegisterResult.Success(token = response.token)
            else -> RegisterResult.Error(Exception("Token Error"))
        }
    }
}