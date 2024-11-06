package com.backend.management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.backend.management.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepo extends MongoRepository<Book, String> {

    Optional<Book> findByIdBook(String idBook);




}