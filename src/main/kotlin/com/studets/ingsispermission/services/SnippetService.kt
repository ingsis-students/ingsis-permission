package com.studets.ingsispermission.services

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class SnippetService(private val restTemplate: RestTemplate) {
    private fun getJsonHeaders(): MultiValueMap<String, String> {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
    }

    fun postDefaultLintRules(userId: Long): ResponseEntity<String> {
        val body: Map<String, Any> = mapOf("userId" to userId)
        val entity = HttpEntity(body, getJsonHeaders())
        return restTemplate.postForEntity(
            "http://localhost:8083/api/lint-rules",
            entity,
            String::class.java
        )
    }
}