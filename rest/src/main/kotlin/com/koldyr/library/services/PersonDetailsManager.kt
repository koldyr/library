package com.koldyr.library.services

import com.koldyr.library.dto.ReaderDetails
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.ReaderRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class PersonDetailsManager(private val readerRepository: ReaderRepository) : UserDetailsManager {

    override fun loadUserByUsername(email: String): UserDetails {
        return readerRepository.findByMail(email)
                .map { ReaderDetails(it) }
                .orElseThrow { UsernameNotFoundException("Reader with email '$email' is not found") }
    }

    @Transactional
    override fun createUser(user: UserDetails) {
        val reader = Reader()
        reader.mail = user.username
        mapUserToReader(reader, user)

        readerRepository.save(reader)
    }

    @Transactional
    override fun updateUser(user: UserDetails) {
        val reader = readerRepository.findByMail(user.username)
                .orElseThrow { UsernameNotFoundException("Reader with email '${user.username}' is not found") }

        mapUserToReader(reader, user)

        readerRepository.save(reader)
    }

    @Transactional
    override fun deleteUser(email: String) {
        val reader = readerRepository.findByMail(email)
                .orElseThrow { UsernameNotFoundException("Reader with email '${email}' is not found") }

        readerRepository.delete(reader)
    }

    override fun changePassword(email: String, password: String) {
        val reader = readerRepository.findByMail(email)
                .orElseThrow { UsernameNotFoundException("Reader with email '${email}' is not found") }

        reader.password = password
        readerRepository.save(reader)
    }

    override fun userExists(email: String): Boolean {
        return readerRepository.findByMail(email).isPresent
    }

    private fun mapUserToReader(reader: Reader, user: UserDetails) {
        reader.password = user.password
        if (user is ReaderDetails) {
            reader.firstName = user.getReader().firstName
            reader.lastName = user.getReader().lastName
            reader.address = user.getReader().address
            reader.phoneNumber = user.getReader().phoneNumber
            reader.note = user.getReader().note
        }
    }
}