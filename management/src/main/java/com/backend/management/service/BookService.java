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

    // lay tat ca cac sach
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    //lay sach theo id
    public Optional<Book> getBookById(String idBook) {
        return bookRepo.findById(idBook);
    }

    // lay sach theo ten hoac tac gia
    public List<Book> searchBooks(String name, String author){
        if(name != null && author != null ){
            return bookRepo.findBookByAuthorAndName(author, name);
        } else if (author != null) {
            return bookRepo.findBookByAuthor(author);
        } else if (name != null) {
            return bookRepo.findBookByName(name);
        }
        return getAllBooks();
    }



    //them sach
    public Book addBook(Book book) {
        return bookRepo.save(book);
    }

    // lay sach theo id va cap nhat sach
    public Book updateBook(String idBook, Book updatedBook) {
        if (bookRepo.existsById(idBook)) {
            updatedBook.setIdBook(idBook);
            return bookRepo.save(updatedBook);
        }
        return null;
    }
    // xoa sach theo id
    public void deleteBook(String idBook) {
        bookRepo.deleteById(idBook);
    }

}