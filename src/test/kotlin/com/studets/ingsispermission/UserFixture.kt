package com.studets.ingsispermission

import com.studets.ingsispermission.entities.Author

object UserFixture {

    fun all(): List<Author> {
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

    val JULIO_CESAR = Author(email = "juliocesar@gmail.com", auth0Id = "auth0-122")
    val MATI_CHIALVA = Author(id = 19000, email = "mati@example.com", auth0Id = "auth0-123")
    val MARCO_AURELIO = Author(email = "marcoaurelio@gmail.com", auth0Id = "auth0-124")
    val AUGUSTUS = Author(email = "augustus@gmail.com", auth0Id = "auth0-125")
    val NERO = Author(email = "nero@gmail.com", auth0Id = "auth0-126")
    val CALIGULA = Author(email = "caligula@gmail.com", auth0Id = "auth0-127")
    val CLAUDIUS = Author(email = "claudius@gmail.com", auth0Id = "auth0-128")
    val TIBERIUS = Author(email = "tiberius@gmail.com", auth0Id = "auth0-129")
}
