package com.backend.management.service;

import com.backend.management.model.Book;
import com.backend.management.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backend.management.exception.BookNotFoundException;

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

    public List<Book> findBookByName(String name) {
        return bookRepo.findByNameOfBook(name);
    }
    public  List<Book> findBookByAuthor(String author) {
        return bookRepo.findByNameOfAuthor(author);
    }
    public List<Book> findBookByCategory(String subCategoryName) {
        return bookRepo.findBySubCategory(subCategoryName);
    }
    public  List<Book> findBookByPublicationYear (Integer publicationYear) {
        return bookRepo.findByPublicationYear(publicationYear);
    }
    public Boolean isBookAvailable (String idBook) {
        Optional<Book> book = bookRepo.findById(idBook);
        return book.map(Book::getAvailability).orElse(false);
    }
    public Book getBookIdOrThrow (String idBook) {
        return bookRepo.findById(idBook)
                .orElseThrow(() -> new BookNotFoundException("Book with id: "+ idBook +  " is not found"));
    }
}
