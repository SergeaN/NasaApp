package ru.sergean.nasaapp.domain.user

import ru.sergean.nasaapp.data.base.ResultWrapper
import ru.sergean.nasaapp.data.user.LoginInfo
import ru.sergean.nasaapp.data.user.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(email: String, password: String): ResultWrapper<LoginInfo> =
        userRepository.login(email, password)
}