package com.studets.ingsispermission.repositories

import com.studets.ingsispermission.entities.Author
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<Author, Long> {
    fun findByEmail(email: String): Author?
    fun findByAuth0Id(auth0Id: String): Author?
}
