// src/main/kotlin/com/library/system/services/UserService.kt
package com.library.system.services

import com.library.system.model.*
import com.library.system.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun registerUser(user: User): User = userRepository.save(user)

    fun getUserById(id: UUID): User =
        userRepository
            .findById(id)
            .orElseThrow { ResourceNotFoundException("User with ID $id not found") }

    fun updateUser(
        id: UUID,
        userDetails: User,
    ): User =
        userRepository
            .findById(id)
            .map { existingUser ->
                val updatedUser =
                    existingUser.copy(
                        name = userDetails.name,
                        email = userDetails.email,
                    )
                userRepository.save(updatedUser)
            }.orElseThrow { ResourceNotFoundException("User with ID $id not found") }
}
