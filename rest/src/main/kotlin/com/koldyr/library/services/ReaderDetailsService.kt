package com.koldyr.library.services

import com.koldyr.library.dto.GrantedPrivilege
import com.koldyr.library.dto.ReaderDetails
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.ReaderRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
open class ReaderDetailsService(private val readerRepository: ReaderRepository) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val reader = readerRepository.findByMail(email)
                .orElseThrow { UsernameNotFoundException("Reader with email '$email' is not found") }

        return mapUserDetails(reader)
    }
    
    private fun mapUserDetails(reader: Reader): ReaderDetails {
        val authorities: MutableSet<GrantedPrivilege> = mutableSetOf()
        for (role in reader.roles) {
            for (privilege in role.privileges) {
                authorities.add(GrantedPrivilege(role.name, privilege.value))
            }
        }
        return ReaderDetails(reader, authorities)
    }
}
