package com.studets.ingsispermission

import com.fasterxml.jackson.databind.ObjectMapper
import com.studets.ingsispermission.controllers.UserController
import com.studets.ingsispermission.entities.Author
import com.studets.ingsispermission.entities.CreateUser
import com.studets.ingsispermission.entities.Snippet
import com.studets.ingsispermission.errors.UserNotFoundException
import com.studets.ingsispermission.repositories.UserRepository
import com.studets.ingsispermission.repositories.UserSnippetsRepository
import com.studets.ingsispermission.services.UserService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ValidationTest {
    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var userSnippetsRepository: UserSnippetsRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val token = "Bearer mocktoken"

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
        val users = UserFixture.all()
        userRepository.saveAll(users)

        val user = Author(id = 1, email = "mati@example.com", auth0Id = "auth0-123", snippets = emptyList())
        whenever(userSnippetsRepository.findByAuthorId(anyLong())).thenReturn(
            listOf(
                Snippet(
                    id = 1, author = user, snippetId = 2, role = "Owner"
                )
            )
        )
    }

    @BeforeEach
    fun printDatabaseContents() {
        val users = userRepository.findAll()
        println("Current users in database:")
        users.forEach {
            println("User ID: ${it.id}, Email: ${it.email}, Auth0 ID: ${it.auth0Id}")
        }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `create user`() {
        val email = "newuser@example.com"
        val auth0Id = "auth0-1234"
        val token = "Bearer mocktoken"

        val newUser = Author(email = email, auth0Id = auth0Id)
        val createUser = CreateUser(email = email)

        val jwt = Mockito.mock(org.springframework.security.oauth2.jwt.Jwt::class.java)
        val claims = mapOf("sub" to auth0Id)

        Mockito.`when`(jwt.claims).thenReturn(claims)
        Mockito.`when`(jwtDecoder.decode(token.removePrefix("Bearer ")))
            .thenReturn(jwt)

        Mockito.`when`(userService.createUser(email, auth0Id))
            .thenReturn(newUser)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/user/")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(createUser))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `should validate and return user id`() {
        val auth0Id = "auth0-123"
        val jwt = Mockito.mock(Jwt::class.java)

        val claims = mapOf("sub" to auth0Id)

        Mockito.`when`(jwtDecoder.decode(token.removePrefix("Bearer "))).thenReturn(jwt)
        Mockito.`when`(jwt.claims).thenReturn(claims)

        val user = Author(email = "user@example.com", auth0Id = auth0Id)

        whenever(userService.getByAuthId(auth0Id)).thenReturn(user)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/user/validate")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(user.id.toString()))
    }
}
