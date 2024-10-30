package com.backend.management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.backend.management.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookRepo extends MongoRepository<Book, String> {
    // Tim sach theo ten
    List<Book> findByName( String name);
    // tim sach theo tac gia
    List<Book> findByAuthor ( String author);


}