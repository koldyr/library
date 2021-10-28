package com.koldyr.library.persistence

import com.koldyr.library.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("roleRepository")
interface RoleRepository : JpaRepository<Role, Int> {
    fun findByName(name: String): Optional<Role>
}
