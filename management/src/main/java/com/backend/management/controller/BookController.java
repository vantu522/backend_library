package com.backend.management.controller;

import com.backend.management.model.Book;
import com.backend.management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")  // Thêm version cho API
@CrossOrigin(origins = "*") // Cho phép CORS
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/{idBook}")
    public ResponseEntity<Book> getBookId(@PathVariable String idBook){
        return bookService.getBookId(idBook)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}