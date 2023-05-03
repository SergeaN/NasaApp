package ru.sergean.nasaapp.domain.user

import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.data.user.RegisterInfo
import ru.sergean.nasaapp.data.user.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        name: String, email: String,
        phoneNumber: String, password: String
    ): ResultWrapper<RegisterInfo> = userRepository.register(name, email, phoneNumber, password)
}