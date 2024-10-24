package com.backend.management.service;


import com.backend.management.model.Book;
import com.backend.management.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepo bookRepo;

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public Optional<Book> getBookId(String idBook){
        return bookRepo.findById(idBook);
    }

    public List<Book> getBookName(String name){
        return bookRepo.findByName(name);
    }

}
