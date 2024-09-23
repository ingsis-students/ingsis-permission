package com.studets.ingsispermission

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.studets.ingsispermission.entities.User
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import java.io.File

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional // rollback after running tests
class HttpRequestTest {

    @LocalServerPort
    private var port: Int = 5003

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @TestFactory
    fun dynamicHttpRequestTests(): Collection<DynamicTest> {
        val creationFolder = "src/test/resources/requests/creations"
        val getFolder = "src/test/resources/requests/gets"

        val creationTests = File(creationFolder).listFiles()?.flatMap { file ->
            if (isTextFile(file)) {
                getParameters(file).flatMap { (requestType, requestBody, expectedResponse) ->
                    when {
                        requestType.contains("create") -> runCreateTest(requestBody, expectedResponse)
                        else -> throw RuntimeException("Unexpected endpoint: $requestType")
                    }
                }
            } else {
                throw IllegalArgumentException("File ${file.name} is not a text file")
            }
        } ?: emptyList()

        val getTests = File(getFolder).listFiles()?.flatMap { file ->
            if (isTextFile(file)) {
                getParameters(file).flatMap { (requestType, _, expectedResponse) ->
                    when {
                        requestType.contains("get") -> runGetTest(file, requestType, expectedResponse)
                        else -> throw RuntimeException("Unexpected endpoint: $requestType")
                    }
                }
            } else {
                throw IllegalArgumentException("File ${file.name} is not a text file")
            }
        } ?: emptyList()

        return creationTests + getTests
    }

    private fun isTextFile(file: File): Boolean {
        return file.isFile && file.extension == "txt"
    }

    private fun getParameters(file: File): List<Triple<String, String, String>> {
        val content = file.readText()
        val parts = content.split("#####").map { it.trim() }

        if (parts.size == 3) {
            return listOf(Triple(parts[0], parts[1], parts[2]))
        }
        if (parts.size == 2) {
            return listOf(Triple(parts[0], "", parts[1]))
        }
        return emptyList()
    }

    fun runGetTest(file: File, requestType: String, expectedResponse: String): List<DynamicTest> {
        val email = requestType.substringAfter("user/").substringBefore("\n")
        return listOf(
            DynamicTest.dynamicTest("GET request $email from ${file.name} should return expected response") {
                val response =
                    restTemplate.getForObject("http://localhost:$port/api/user/$email", String::class.java)

                // armo user a partir de la response.
                val mapper = jacksonObjectMapper()
                val expectedUser = mapper.readValue(expectedResponse, User::class.java)
                val actualUser = mapper.readValue(response, User::class.java)

                assertEquals(expectedUser.email, actualUser.email, "Email should match")
            }
        )
    }

    fun runCreateTest(requestBody: String, expectedResponse: String): List<DynamicTest> {
        return listOf(
            DynamicTest.dynamicTest("CREATE request should return expected response") {
                val headers = HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
                val entity = HttpEntity(requestBody, headers)

                val response =
                    restTemplate.postForEntity("http://localhost:$port/api/user", entity, String::class.java)

                // armo user a partir de la response.
                val mapper = jacksonObjectMapper()
                val expectedUser = mapper.readValue(expectedResponse, User::class.java)
                val actualUser = mapper.readValue(response.body, User::class.java)

                assertEquals(expectedUser.email, actualUser.email, "Email should match")
            }
        )
    }
}
