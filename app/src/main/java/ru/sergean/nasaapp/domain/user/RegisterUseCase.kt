package ru.sergean.nasaapp.domain.user

import ru.sergean.nasaapp.data.user.UserRepository
import javax.inject.Inject


class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        name: String, email: String, phoneNumber: String, password: String
    ) = userRepository.register(name, email, phoneNumber, password)
}