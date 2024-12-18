package com.studets.ingsispermission.controllers

import com.studets.ingsispermission.entities.CreateUser
import com.studets.ingsispermission.entities.Author
import com.studets.ingsispermission.entities.dtos.UserDTO
import com.studets.ingsispermission.entities.dtos.UserSnippetDto
import com.studets.ingsispermission.entities.request_types.CheckRequest
import com.studets.ingsispermission.entities.request_types.UserSnippet
import com.studets.ingsispermission.errors.UserNotFoundException
import com.studets.ingsispermission.routes.UserControllerRoutes
import com.studets.ingsispermission.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val jwtDecoder: JwtDecoder,
) : UserControllerRoutes {

    @PostMapping("/")
    override fun create(
        @RequestHeader("Authorization") token: String,
        @RequestBody createUser: CreateUser
    ): ResponseEntity<Author> {
        val auth0Id = jwtDecoder.decode(token.removePrefix("Bearer ")).claims["sub"] as String

        val existingUser = try {
            userService.getByEmail(createUser.email)
        } catch (e: UserNotFoundException) {
            null
        }
        return if (existingUser != null) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(existingUser)
        } else {
            val newUser = userService.createUser(createUser.email, auth0Id)
            ResponseEntity.ok(newUser)
        }
    }

    @GetMapping("/get/{id}")
    override fun getUserById(@PathVariable id: Long): ResponseEntity<UserDTO> {
        val user = userService.getById(id)
        return ResponseEntity.ok(UserDTO(user))
    }

    @GetMapping("/auth0/{auth0Id}")
    override fun getUserByAuth0Id(@PathVariable auth0Id: String): ResponseEntity<UserDTO> {
        val user = userService.getByAuthId(auth0Id)
        return ResponseEntity.ok(UserDTO(user!!))
    }

    @GetMapping("/")
    override fun getAllUsers(): ResponseEntity<List<UserDTO>> {
        val users = userService.getAllUsers()
        val usersDTO = users.map { user -> UserDTO(user) }
        return ResponseEntity.ok(usersDTO)
    }

    @PutMapping("/{email}")
    override fun updateUser(@RequestBody author: Author): ResponseEntity<Author> {
        return ResponseEntity.ok(userService.updateUser(author))
    }

    @GetMapping("/get-user-snippets/{userId}")
    override fun getUserSnippets(@PathVariable userId: String): ResponseEntity<List<UserSnippetDto>> {
        val snippets = userService.getSnippetsOfUser(userId)
        return ResponseEntity.ok(snippets)
    }

    @PostMapping("/add-snippet/{email}")
    override fun addSnippetToUser(
        @PathVariable email: String,
        @RequestBody addSnippet: UserSnippet
    ): ResponseEntity<String> {
        return userService.addSnippetToUser(email, addSnippet.snippetId, addSnippet.role)
    }

    @PostMapping("/check-owner")
    override fun checkIfOwner(@RequestBody checkRequest: CheckRequest): ResponseEntity<String> {
        return userService.checkIfOwner(checkRequest.snippetId, checkRequest.email)
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
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/snippets/{id}")
    override fun getUserSnippetsId(@PathVariable id: Long): ResponseEntity<List<Long>> {
        return userService.getSnippetsId(id)
    }

    @GetMapping("/{email}") // once implemented auth0, this would be auth0Id.
    override fun getUserByEmail(@PathVariable email: String): ResponseEntity<Author> {
        return ResponseEntity.ok(userService.getByEmail(email)!!)
    }
}
