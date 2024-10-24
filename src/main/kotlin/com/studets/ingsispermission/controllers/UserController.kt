package com.studets.ingsispermission.controllers

import com.studets.ingsispermission.entities.Snippet
import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.entities.request_types.CheckRequest
import com.studets.ingsispermission.entities.request_types.UserSnippet
import com.studets.ingsispermission.routes.UserControllerRoutes
import com.studets.ingsispermission.security.OAuth2ResourceServerSecurityConfiguration
import com.studets.ingsispermission.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService,
                     private val jwtDecoder: JwtDecoder) : UserControllerRoutes {
    @PostMapping
    override fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(userService.createUser(user.email, user.auth0Id))
    }

    @GetMapping("/{email}") // once implemented auth0, this would be auth0Id.
    override fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        return ResponseEntity.ok(userService.getByEmail(email)!!)
    }

    @GetMapping
    override fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @PutMapping("/{email}")
    override fun updateUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(userService.updateUser(user))
    }

    @PostMapping("/add-snippet/{email}")
    override fun addSnippetToUser(@PathVariable email: String, @RequestBody addSnippet: UserSnippet): ResponseEntity<String> {
        return userService.addSnippetToUser(email, addSnippet.snippetId, addSnippet.role)
    }

    @PostMapping("/check-owner")
    override fun checkIfOwner(@RequestBody checkRequest: CheckRequest): ResponseEntity<String> {
        return if (userService.checkIfOwner(checkRequest.snippetId, checkRequest.email)) {
            ResponseEntity.ok("User is the owner of the snippet")
        } else {
            ResponseEntity.badRequest().body("User is not the owner of the snippet")
        }
    }

    @GetMapping("/validate")
    override fun validate(@RequestHeader("Authorization") token: String): ResponseEntity<Long> {
        val actualToken = token.removePrefix("Bearer ")
        val jwt = jwtDecoder.decode(actualToken)
        val auth0Id = jwt.claims["sub"] as String

        val user = userService.getByAuthId(auth0Id)
        return if (user != null) {
            ResponseEntity.ok(user.id)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build() // no body.
        }
    }

    @GetMapping("/snippets")
    override fun getUserSnippets(id: Long): ResponseEntity<List<Snippet>> {
        return userService.getSnippets(id)
    }
}
