package ru.sergean.nasaapp.data.user

import ru.sergean.nasaapp.data.base.ResultWrapper
import javax.inject.Inject

class UserRepository @Inject constructor(private val userService: UserService) {

    suspend fun login(email: String, password: String): ResultWrapper<LoginInfo> {
        val receiveData = LoginReceive(email, password)
        return try {
            val loginInfo = userService.login(receiveData).mapToInfo()
            ResultWrapper.Success(loginInfo)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "Error")
        }
    }

    suspend fun register(
        name: String, email: String, phoneNumber: String, password: String
    ): ResultWrapper<RegisterInfo> {
        val receiveData = RegisterReceive(name, email, phoneNumber, password)
        return try {
            val registerInfo = userService.register(receiveData).mapToInfo()
            ResultWrapper.Success(registerInfo)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "Error")
        }
    }
}