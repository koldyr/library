package com.koldyr.library.controllers

import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.WWW_AUTHENTICATE
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import com.koldyr.library.dto.CredentialsDTO
import com.koldyr.library.dto.ReaderDTO

/**
 * Description of the ReaderControllerSecurityTest class
 *
 * @author d.halitski@gmail.com
 * @created 2023-02-15
 */
class ReaderControllerSecurityTest : LibraryControllerTest() {
    
    @Test
    fun registerExistingUser() {
        val user =  ReaderDTO()
        user.mail = userName
        user.password = randomAlphabetic(10)
        user.firstName = randomAlphabetic(10)
        user.lastName = randomAlphabetic(10)

        rest.post("/api/v1/registration") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            content = mapper.writeValueAsString(user)
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isBadRequest()
                    reason("Reader with mail '${user.mail}' already exists")
                }
            }
    }

    @Test
    fun registerUserNoPassword() {
        val user =  ReaderDTO()
        user.mail = "test@koldyr.com"
        user.firstName = randomAlphabetic(10)
        user.lastName = randomAlphabetic(10)

        rest.post("/api/v1/registration") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            content = mapper.writeValueAsString(user)
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isBadRequest()
                    reason("Reader password must be provided")
                }
            }
    }

    @Test
    fun wrongPassword() {
        val login = CredentialsDTO(userName, "12345")

        rest.post("/api/v1/login") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(login)
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isUnauthorized()
                    reason("Bad credentials")
                }
            }
    }

    @Test
    fun wrongUser() {
        val username = randomAlphabetic(5) + "@mail.com"
        val login = CredentialsDTO(username, randomAlphabetic(10))

        rest.post("/api/v1/login") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(login)
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isUnauthorized()
                    reason("Bad credentials")
                }
            }
    }

    @Test
    fun wrongToken() {
        rest.get("/api/v1/readers/me") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, randomAlphabetic(20))
        }
//            .andDo { print() }
            .andExpect {
                status { isUnauthorized() }
                header { string(WWW_AUTHENTICATE, Matchers.`is`("Bearer")) }
            }
    }

    @Test
    fun noToken() {
        rest.get("/api/v1/readers/me") {
            accept = APPLICATION_JSON
        }
//            .andDo { print() }
            .andExpect {
                status { isUnauthorized() }
                header { string(WWW_AUTHENTICATE, Matchers.`is`("Bearer")) }
            }
    }

    @Test
    fun wrongAccessLevel() {
        val author = createAuthor()
        val book = createBook(author)
        book.bookCover = randomAlphabetic(10)
        book.note = randomAlphabetic(10)

        val reader = createReader()
        val readerToken = login(CredentialsDTO(reader.mail, reader.password))

        rest.put("/api/v1/books/${book.id}") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            header(AUTHORIZATION, readerToken)
            content = mapper.writeValueAsString(book)
        }
//            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                header { string(WWW_AUTHENTICATE, containsString("insufficient_scope")) }
            }
    }

}
