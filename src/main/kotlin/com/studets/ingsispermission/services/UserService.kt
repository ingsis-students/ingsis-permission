package com.studets.ingsispermission.services

import com.studets.ingsispermission.entities.Snippet
import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.errors.UserNotFoundException
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
            ?: throw UserNotFoundException("User not found when trying to get by email")
        return user
    }

    fun getByAuthId(auth0Id: String): User? {
        return userRepository.findByAuth0Id(auth0Id)
            ?: throw UserNotFoundException("User not found when trying to get by auth0Id")
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
            throw UserNotFoundException("User not found when trying to update it")
        }
        val updatedUser = userOptional.get().copy(email = user.email, auth0Id = user.auth0Id)
        userRepository.save(updatedUser)
        return updatedUser
    }

    fun addSnippetToUser(email: String, snippetId: Long, role: String): ResponseEntity<String> {
        val user = getByEmail(email) ?: throw UserNotFoundException("User not found when trying to add a snippet to it")
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
        val user = getByEmail(email)
            ?: throw UserNotFoundException("User not found when trying to check if it is the owner of a snippet")

        userSnippetsRepository.findByUserId(user.id!!).forEach {
            if (it.snippetId == snippetId) {
                return it.role == "Owner"
            }
        }
        return false
    }

    fun getSnippets(id: Long): ResponseEntity<List<Snippet>> {
        val user = userRepository.findById(id).orElseThrow { UserNotFoundException("User not found when trying to get snippets") }
        val snippets = userSnippetsRepository.findByUserId(user.id!!)
        return ResponseEntity.ok(snippets)
    }
}
