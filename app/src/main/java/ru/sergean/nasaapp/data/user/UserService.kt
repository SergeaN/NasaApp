package ru.sergean.nasaapp.data.user

import kotlinx.coroutines.delay
import retrofit2.http.Body
import retrofit2.http.POST

private const val DEVICE_ADDRESS = "192.168.0.104:8080"
private const val EMULATOR_ADDRESS = "10.0.2.2:8080"

const val USER_SERVICE_URL = "http://$EMULATOR_ADDRESS"

interface UserService {

    @POST("/login")
    suspend fun login(@Body loginReceive: LoginReceive): LoginResponse

    @POST("/register")
    suspend fun register(@Body registerReceive: RegisterReceive): RegisterResponse
}

object MockUserService : UserService {
    override suspend fun login(loginReceive: LoginReceive): LoginResponse {
        delay(timeMillis = 3000)
        return LoginResponse(token = "qweqwewq", error = null)
    }

    override suspend fun register(registerReceive: RegisterReceive): RegisterResponse {
        delay(timeMillis = 3000)
        return RegisterResponse(token = null, error = "Some Error")
    }
}
