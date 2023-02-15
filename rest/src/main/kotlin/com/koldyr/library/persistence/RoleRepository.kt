package com.koldyr.library.persistence

import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.koldyr.library.model.Role

/**
 * Description of class RoleRepository
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-11-09
 */
@Repository("roleRepository")
interface RoleRepository : JpaRepository<Role, Int> {
    fun findByName(name: String): Optional<Role>
}
