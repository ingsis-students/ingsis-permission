package com.studets.ingsispermission.services

import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.repositories.UserRepository

class UserService(private val userRepository: UserRepository) {
    fun getByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun createUser(email: String, auth0Id: String? = null): User {
        val user = User(email = email, auth0Id = auth0Id)
        return userRepository.save(user)
    }

    fun updateUser(user: User): User {
        return userRepository.save(user)
    }

    fun deleteUser(user: User) {
        userRepository.delete(user)
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }
}