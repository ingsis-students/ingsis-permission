package com.studets.ingsispermission.repositories

import com.studets.ingsispermission.entities.Snippet
import org.springframework.data.jpa.repository.JpaRepository

interface UserSnippetsRepository : JpaRepository<Snippet, Long> {
    fun findByUserId(userId: Long): List<Snippet>
}
