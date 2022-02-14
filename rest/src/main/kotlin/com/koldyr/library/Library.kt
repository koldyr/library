package com.koldyr.library

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Description of class Library
 * @created: 2021-09-25
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.koldyr.library.persistence"])
@EntityScan("com.koldyr.library.model")
open class Library

fun main(args: Array<String>) {
    runApplication<Library>(*args)
}
