package com.koldyr.library.controllers

import com.koldyr.library.Library
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc


/**
 * Description of class AuthorControllerTest
 * @created: 2021-10-21
 */
@RunWith(value = SpringRunner::class)
@SpringBootTest(classes = [Library::class])
@AutoConfigureMockMvc
@Sql(scripts = ["/sql/schema.sql", "/sql/initial-data.sql"])
@TestPropertySource(locations = ["classpath:application-test.yaml"])
class AuthorControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    @Test
    fun authors() {
    }

    @Test
    fun crud() {
    }

    @Test
    fun books() {
    }
}
