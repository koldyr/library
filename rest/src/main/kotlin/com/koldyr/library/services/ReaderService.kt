package com.koldyr.library.services

import com.koldyr.library.model.Reader

/**
 * Description of class ReaderService
 * @created: 2021-09-28
 */
interface ReaderService {
    fun create(person: Reader): Int
    fun findAll(): List<Reader>
    fun findById(personId: Int): Reader
    fun update(readeId: Int, reader: Reader)
    fun delete(readerId: Int)
}
