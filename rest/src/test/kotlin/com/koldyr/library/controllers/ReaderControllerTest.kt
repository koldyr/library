package com.koldyr.library.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.koldyr.library.Library
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Description of class ReaderControllerTest
 * @created: 2021-10-24
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Library::class])
@AutoConfigureMockMvc
@TestPropertySource(properties = ["spring.config.location = classpath:application-test.yaml"])
@IfProfileValue(name = "spring.profiles.active", values = ["int-test"])
class ReaderControllerTest {
    
    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var rest: MockMvc

    @Test
    fun readers() {
        rest.get("/api/library/readers")
            .andDo { print() }
            .andExpect { status { isOk() } }
    }

    @Test
    fun orders() {
        rest.get("/api/library/readers/1/orders")
            .andDo { print() }
            .andExpect { status { isOk() } }
    }

    @Test
    fun feedbacks() {
        rest.get("/api/library/readers/1/feedbacks")
            .andDo { print() }
            .andExpect { status { isOk() } }
    }
}