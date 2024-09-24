package com.studets.ingsispermission.controllers

import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {
    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(userService.createUser(user.email, user.auth0Id))
    }

    @GetMapping("/{email}") // once implemented auth0, this would be auth0Id.
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        return ResponseEntity.ok(userService.getByEmail(email)!!)
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @PutMapping("/{email}")
    fun updateUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(userService.updateUser(user))
    }

    @PostMapping("/add-snippet/{email}")
    fun addSnippetToUser(@PathVariable email: String, @RequestBody addSnippet: UserSnippet): ResponseEntity<String> {
        userService.addSnippetToUser(email, addSnippet.snippetId, addSnippet.role)
        return ResponseEntity.ok("Snippet added to user")
    }

    @PostMapping("/check-owner")
    fun checkIfOwner(@RequestBody checkRequest: CheckRequest): ResponseEntity<String> {
        return if (userService.checkIfOwner(checkRequest.snippetId, checkRequest.email)) {
            ResponseEntity.ok("User is the owner of the snippet")
        } else {
            ResponseEntity.badRequest().body("User is not the owner of the snippet")
        }
    }
}

data class UserSnippet(
    val snippetId: Long,
    val role: String
)

data class CheckRequest(
    val snippetId: Long,
    val email: String
)
