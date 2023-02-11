package com.koldyr.library.services

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import com.koldyr.library.dto.ReaderDetails
import com.koldyr.library.persistence.ReaderRepository

@Service
class ReaderDetailsService(
    private val readerRepository: ReaderRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        return readerRepository.findByMail(email)
            .map { ReaderDetails(it) }
            .orElseThrow { UsernameNotFoundException("Reader with email '$email' is not found") }
    }
}
