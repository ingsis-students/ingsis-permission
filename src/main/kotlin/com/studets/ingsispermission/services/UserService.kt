package com.studets.ingsispermission.services

import com.studets.ingsispermission.entities.Snippet
import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.repositories.UserRepository
import com.studets.ingsispermission.repositories.UserSnippetsRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userSnippetsRepository: UserSnippetsRepository
) {
    fun getByEmail(email: String): User? {
        val user = userRepository.findByEmail(email)
        if (user == null) {
            throw NoSuchElementException("User not found")
        }
        return user
    }

    fun createUser(email: String, auth0Id: String): User {
        val user = User(email = email, auth0Id = auth0Id)
        return userRepository.save(user)
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun updateUser(user: User): User? {
        val userOptional = userRepository.findById(user.id!!)
        if (userOptional.isEmpty) {
            throw NoSuchElementException("User not found")
        }
        val updatedUser = userOptional.get().copy(email = user.email, auth0Id = user.auth0Id)
        userRepository.save(updatedUser)
        return updatedUser
    }

    fun addSnippetToUser(email: String, snippetId: Long, role: String): ResponseEntity<String> {
        val user = getByEmail(email) ?: throw NoSuchElementException("User not found")
        val snippets = Snippet(user = user, snippetId = snippetId, role = role)

        if (userSnippetRelationExists(user, snippetId)) {
            return ResponseEntity.ok("User already has this snippet.")
        }

        try {
            userSnippetsRepository.save(snippets)
            return ResponseEntity.ok("Snippet added to user")
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Error adding snippet to user")
        }
    }

    private fun userSnippetRelationExists(user: User, snippetId: Long): Boolean {
        userSnippetsRepository.findByUserId(user.id!!).forEach {
            if (it.snippetId == snippetId) {
                return true
            }
        }
        return false
    }

    fun checkIfOwner(snippetId: Long, email: String): Boolean {
        val user = getByEmail(email) ?: throw NoSuchElementException("User not found")
        userSnippetsRepository.findByUserId(user.id!!).forEach {
            if (it.snippetId == snippetId) {
                return it.role == "Owner"
            }
        }
        return false
    }
}
