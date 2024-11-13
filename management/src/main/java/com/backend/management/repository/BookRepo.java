package com.backend.management.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.backend.management.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BookRepo extends MongoRepository<Book, String> {

    Optional<Book> findByBookId(String bookId);

    @Aggregation(pipeline = {
            "{ '$group': { '_id': null, 'totalQuantity': { '$sum': '$quantity' } } }"
    })
    List<Map<String, Object>> getTotalBooksInStock();




}