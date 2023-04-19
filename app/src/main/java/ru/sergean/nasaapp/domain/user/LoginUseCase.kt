package ru.sergean.nasaapp.domain.user

import ru.sergean.nasaapp.data.user.LoginResult
import ru.sergean.nasaapp.data.user.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(email: String, password: String): LoginResult =
        userRepository.login(email, password)
}