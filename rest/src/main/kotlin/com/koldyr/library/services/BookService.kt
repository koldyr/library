package com.koldyr.library.services

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.PageResultDTO
import com.koldyr.library.dto.SearchCriteria

/**
 * Description of class BookService
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-09-28
 */
interface BookService {
    fun findAll(available: Boolean): List<BookDTO>
    fun create(book: BookDTO): Int
    fun findById(bookId: Int): BookDTO
    fun update(bookId: Int, book: BookDTO)
    fun delete(bookId: Int)

    fun findBooks(authorId: Int): List<BookDTO>
    fun findBooks(criteria: SearchCriteria?): PageResultDTO<BookDTO>

    fun takeBook(order: OrderDTO): OrderDTO
    fun returnBook(order: OrderDTO)

    fun feedbackBook(feedback: FeedbackDTO): Int
    fun bookFeedbacks(bookId: Int): List<FeedbackDTO>
    fun deleteFeedback(feedbackId: Int)

    fun bookOrders(bookId: Int): Collection<OrderDTO>
}
