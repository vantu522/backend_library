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

    public Optional<Book> getBookById(String id) {
        return bookRepo.findById(id);
    }

    public Book addBook(Book book) {
        return bookRepo.save(book);
    }

    public Book updateBook(String id, Book updatedBook) {
        if (bookRepo.existsById(id)) {
            updatedBook.setIdBook(id);
            return bookRepo.save(updatedBook);
        }
        return null;
    }

    public void deleteBook(String id) {
        bookRepo.deleteById(id);
    }
}