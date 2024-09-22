package com.studets.ingsispermission.controllers

import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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


}