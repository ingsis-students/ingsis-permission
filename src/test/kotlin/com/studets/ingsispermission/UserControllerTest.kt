package com.studets.ingsispermission

import com.studets.ingsispermission.controllers.UserController
import com.studets.ingsispermission.entities.Snippet
import com.studets.ingsispermission.entities.Author
import com.studets.ingsispermission.entities.request_types.CheckRequest
import com.studets.ingsispermission.entities.request_types.UserSnippet
import com.studets.ingsispermission.repositories.UserRepository
import com.studets.ingsispermission.repositories.UserSnippetsRepository
import com.studets.ingsispermission.services.SnippetService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import java.util.Optional
import kotlin.test.assertFailsWith

@SpringBootTest()
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private lateinit var userController: UserController

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var userSnippetsRepository: UserSnippetsRepository

    @MockBean
    private lateinit var snippetService: SnippetService

    @BeforeEach
    fun setup() {
        val users = UserFixture.all()
        val author = Author(id = 1, email = "mati@example.com", auth0Id = "auth0-123", snippets = emptyList())
        userRepository.saveAll(users)

        whenever(userRepository.findById(1)).thenAnswer { Optional.of(UserFixture.MATI_CHIALVA) }
        whenever(userRepository.findByEmail("mati@example.com")).thenAnswer { author }
        whenever(userRepository.findByAuth0Id("auth0-122")).thenAnswer { UserFixture.MATI_CHIALVA }
        whenever(userRepository.save(any<Author>())).thenAnswer { UserFixture.MATI_CHIALVA }
        whenever(userRepository.findAll()).thenAnswer { users }
        whenever(userSnippetsRepository.findByAuthorId(anyLong())).thenAnswer {
            listOf(Snippet(id = 1, author = author, snippetId = 2, role = "Owner"))
        }
        whenever(snippetService.postDefaultLintRules(anyLong())).thenReturn(ResponseEntity("Default lint rules applied", HttpStatus.OK))
        whenever(snippetService.postDefaultFormatRules(anyLong())).thenReturn(ResponseEntity("Default lint rules applied", HttpStatus.OK))
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"]) // mock security
    fun `can get user by email`() {
        val user = userController.getUserByEmail("mati@example.com")
        assertNotNull(user, "User should not be null")
    }

    @Test
    fun `can get user by auth0id`() {
        val user = userRepository.findByAuth0Id("auth0-122")
        assertNotNull(user, "User should not be null")
    }

    @Test
    fun `fails to retrieve non existent user`() {
        val user = userRepository.findByAuth0Id("10")
        assertTrue(user == null, "User should be null")
    }

//    @Test
//    @WithMockUser(authorities = ["SCOPE_read:snippets"]) // mock security
//    fun `can create user`() {
//        val token =
//        val user = CreateUser(email = "nacho@example.com")
//        val response = userController.create(token, user)
//
//        assertNotNull(response.body?.email, "User email should not be null")
//    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `find all should return all users`() {
        val users = userController.getAllUsers().body!!
        assertEquals(8, users.size)
        assertTrue(users.any { it.email == "mati@example.com" })
        assertTrue(users.any { it.email == "juliocesar@gmail.com" })
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `can update user`() {
        val author = Author(id = 1, email = "mati@gmail.com", auth0Id = "auth0-123")
        val response = userController.updateUser(author)
        assertTrue(response.body?.email == "mati@gmail.com")
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `can add snippet to user`() {
        val response = userController.addSnippetToUser("mati@example.com", UserSnippet(1, "admin"))
        assertTrue(response.body == "Snippet added to user")
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `can check if user is owner of snippet`() {
        val response = userController.checkIfOwner(CheckRequest(2, "mati@example.com"))
        assertTrue(response.body == "User is the owner of the snippet")
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `find user non-existent throws exception`() {
        val exception = assertFailsWith<Exception> {
            userController.getUserByEmail("nonexistent@gmail.com")
        }
        assert(exception.message?.contains("User not found when trying to get by email") == true)
    }
}
