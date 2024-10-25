package com.backend.management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.backend.management.model.Book;

public interface BookRepo extends MongoRepository<Book, String> {
}
