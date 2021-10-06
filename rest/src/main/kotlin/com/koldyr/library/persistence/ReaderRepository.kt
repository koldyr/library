package com.koldyr.library.persistence

import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Description of class ReaderRepository
 * @created: 2021-09-25
 */
@Repository("readerRepository")
interface ReaderRepository : JpaRepository<Reader, Int> {
    @Query("select p.orders from Reader as p where p.id = :personId")
    fun findEvents(@Param("personId") personId: Int): Collection<Order>
}
