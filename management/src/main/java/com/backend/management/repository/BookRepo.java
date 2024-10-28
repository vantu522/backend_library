package com.backend.management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.backend.management.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookRepo extends MongoRepository<Book, String> {
    // Tim sach theo ten
    List<Book> findByName( String name);
//    //Tim sach theo ten tac gia
//    List<Book> findByAuthor(String author);
//    //Tim sach theo nam xuat ban
//    List<Book> findByPublicationYear (Integer publicationYear);
//    // Tim sach theo the loai
//    List<Book> findBySubCategory(String subCategoryNames);

}