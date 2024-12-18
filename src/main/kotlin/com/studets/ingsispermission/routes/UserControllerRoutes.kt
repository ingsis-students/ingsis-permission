package com.studets.ingsispermission.routes

import com.studets.ingsispermission.entities.CreateUser
import com.studets.ingsispermission.entities.Author
import com.studets.ingsispermission.entities.dtos.UserDTO
import com.studets.ingsispermission.entities.dtos.UserSnippetDto
import com.studets.ingsispermission.entities.request_types.CheckRequest
import com.studets.ingsispermission.entities.request_types.UserSnippet
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

/** This interface is used to define the routes of the user controller */
interface UserControllerRoutes {

    /** This method is used to create a user */
    @PostMapping
    fun create(@RequestHeader token: String, @RequestBody createUser: CreateUser): ResponseEntity<Author>

    /** This method is used to get a user by its id */

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDTO>
    /** This method is used to get a user by his auth0Id */
    @GetMapping("/auth0/{auth0Id}")
    fun getUserByAuth0Id(@PathVariable auth0Id: String): ResponseEntity<UserDTO>

    /** This method is used to get a user by email */
    @GetMapping("/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<Author>

    /** This method is used to get all users */
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDTO>>

    /** This method is used to get the snippets of a user */
    @GetMapping("/get-user-snippets")
    fun getUserSnippets(@RequestBody email: String): ResponseEntity<List<UserSnippetDto>>

    /** This method is used to update a user */
    @PutMapping("/{email}")
    fun updateUser(@RequestBody author: Author): ResponseEntity<Author>

    /** This method is used to add a snippet to a user */
    @PostMapping("/add-snippet/{email}")
    fun addSnippetToUser(@PathVariable email: String, @RequestBody addSnippet: UserSnippet): ResponseEntity<String>

    /** This method is used to check if a user is the owner of a snippet */
    @PostMapping("/check-owner")
    fun checkIfOwner(@RequestBody checkRequest: CheckRequest): ResponseEntity<String>

    /** This method is used to validate a user in auth */
    @GetMapping("/validate")
    fun validate(@AuthenticationPrincipal token: String): ResponseEntity<Long>

    @GetMapping("/snippets")
    fun getUserSnippetsId(id: Long): ResponseEntity<List<Long>>
}
