package com.studets.ingsispermission

import com.studets.ingsispermission.entities.User

object UserFixture {

    fun all(): List<User> {
        return listOf(
            MATI_CHIALVA,
            JULIO_CESAR,
            MARCO_AURELIO,
            AUGUSTUS,
            NERO,
            CALIGULA,
            CLAUDIUS,
            TIBERIUS
        )
    }

    val JULIO_CESAR = User(email = "juliocesar@gmail.com", auth0Id = "auth0-122")
    val MATI_CHIALVA = User(email = "mati@example.com", auth0Id = "auth0-123")
    val MARCO_AURELIO = User(email = "marcoaurelio@gmail.com", auth0Id = "auth0-124")
    val AUGUSTUS = User(email = "augustus@gmail.com", auth0Id = "auth0-125")
    val NERO = User(email = "nero@gmail.com", auth0Id = "auth0-126")
    val CALIGULA = User(email = "caligula@gmail.com", auth0Id = "auth0-127")
    val CLAUDIUS = User(email = "claudius@gmail.com", auth0Id = "auth0-128")
    val TIBERIUS = User(email = "tiberius@gmail.com", auth0Id = "auth0-129")
}
