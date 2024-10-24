package com.backend.management.repository;


import com.backend.management.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepo extends MongoRepository<Book, String> {

    Optional<Book> findByBookId(String idBook);

    List<Book> findByName(String name);
    List<Book> findByAuthorContaining(String author);
    List<Book> findByBigCategoryContaining(String category);
    List<Book> findBySmallCategoryContaining(String category);

}
