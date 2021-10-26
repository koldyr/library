package com.koldyr.library.persistence

import com.koldyr.library.model.Authority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("authorityRepository")
interface AuthorityRepository : JpaRepository<Authority, Int> {
    fun findByValue(value: String): Optional<Authority>
}
