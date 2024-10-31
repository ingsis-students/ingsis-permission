package com.studets.ingsispermission.entities.dtos

import com.studets.ingsispermission.entities.User

data class UserDTO(
    val id: Long?,
    val email: String,
    val auth0Id: String
) {
    constructor(user: User) : this(
        id = user.id ?: 0L,
        email = user.email,
        auth0Id = user.auth0Id
    )
}
