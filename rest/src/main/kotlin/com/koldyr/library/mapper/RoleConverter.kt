package com.koldyr.library.mapper

import java.util.Objects.isNull
import com.koldyr.library.model.Role
import com.koldyr.library.persistence.RoleRepository
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class RoleConverter
 * @created: 2022-02-14
 */
class RoleConverter(private val roleRepository: RoleRepository) : BidirectionalConverter<String, Role>() {

    override fun convertTo(role: String?, type: Type<Role>?, context: MappingContext?): Role? {
        if (isNull(role)) {
            return null
        }
        return roleRepository.findByName(role!!.lowercase())
                .orElseThrow { ResponseStatusException(BAD_REQUEST, "Role with id '$role' is not found") }
    }

    override fun convertFrom(role: Role?, type: Type<String>?, context: MappingContext?): String? {
        return role?.name
    }
}
