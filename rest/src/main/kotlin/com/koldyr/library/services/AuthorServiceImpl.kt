package com.koldyr.library.services

import com.koldyr.library.model.Author
import org.springframework.stereotype.Service

@Service("authorService")
class AuthorServiceImpl : AuthorService {
    override fun findAll(): List<Author> {
        TODO("Not yet implemented")
    }

    override fun create(author: Author): Int {
        TODO("Not yet implemented")
    }

    override fun findById(authorId: Int): Author {
        TODO("Not yet implemented")
    }

    override fun update(authorId: Int, author: Author) {
        TODO("Not yet implemented")
    }

    override fun delete(authorId: Int) {
        TODO("Not yet implemented")
    }
}
