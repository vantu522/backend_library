package com.backend.management.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.management.exception.BookServiceException;
import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.model.PaginatedResponse;
import com.backend.management.service.BookCategoryService;
import com.backend.management.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCategoryService bookCategoryService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy) {
        try {
            Page<Book> books = bookService.getAllBooks(page, size, sortBy);
            PaginatedResponse<Book> response = PaginatedResponse.of(books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{bookId}")
    public ResponseEntity<Book> updateBook(
            @PathVariable String bookId,
            @RequestBody Book updatedBook
    ) {
        try {
            logger.info("Received update request for book ID: {}", bookId);
            logger.debug("Request body: {}", updatedBook);

            Book book = bookService.updateBook(bookId, updatedBook);
            return ResponseEntity.ok(book);

        } catch (BookServiceException ex) {
            logger.error("BookServiceException while updating book: {}", bookId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } catch (Exception ex) {
            logger.error("Unexpected error while updating book: {}", bookId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        try {
            if (book == null || book.getTitle() == null || book.getTitle().isEmpty()) {
                logger.warn("Received book with missing title");
                return ResponseEntity.badRequest().body(null);
            }

            logger.info("Received request to add book: {}", book.getTitle());
            Book savedBook = bookService.addBook(book);
            logger.info("Successfully added book with ID: {}", savedBook.getBookId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);

        } catch (Exception Ex) {
            logger.error("Error occurred while adding book: {}", Ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{bookId}")
    public Optional<Book> getBookByBookId(@PathVariable String bookId) {
        return bookService.getBookByBookId(bookId);
    }

    @GetMapping("/categories/{bigCategoryName}/{subCategoryName}/books")
    public ResponseEntity<List<Book>> getBooksBySubCategory(@PathVariable String subCategoryName,
                                                            @PathVariable String bigCategoryName){
        List<Book> books = bookService.getBooksBySubCategory(subCategoryName, bigCategoryName);
        return ResponseEntity.ok(books);
    }


    @DeleteMapping("/delete/{bookId}")
    public void deleteBook(@PathVariable String bookId) {
        bookService.deleteBook(bookId);
    }

    // lay ca the loai lon
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getBigCategories() {
        List<String> bigCategories = bookCategoryService.getAllBigCategories();
        return ResponseEntity.ok(bigCategories);
    }

    @GetMapping("/categories/{bigCategorySlug}")
    public  List<String> getSmallCategories(@PathVariable String bigCategorySlug){
        return bookCategoryService.getSmallCategories(bigCategorySlug);
    }

    @GetMapping("/{bookId}/availability")
    public boolean checkAvaibility(@PathVariable String bookId){
        return bookService.isBookAvailable(bookId);
    }

    //tong so luong sach dang co
    @GetMapping("/total")
    public int getTotalBooksInStock(){
        return bookService.getTotalBooksInStock();
    }

    @GetMapping("/category-distribution")
    public Map<String, Long> getCategoryDistribution() {
        return bookService.getCategoryDistribution();
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String title,
                                  @RequestParam(required = false) String author)
    {
        return bookService.searchBooks(title,author);
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<Book>> suggestBooks(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(bookService.suggestBooks(query));
    }
    // sua danh muc lon
    @PutMapping("/update-big-category")
    public ResponseEntity<String> updateBigCategoryName(
            @RequestParam String oldName,
            @RequestParam String newName){

        bookService.updateBigCategoryName(oldName,newName);
        return ResponseEntity.ok("success");
    }

    //xoa danh muc lon
    @DeleteMapping("delete-big-category")
    public ResponseEntity<String> deleteBigCategory(@RequestParam  String bigCategoryName){
        bookService.deleteBigCategoryName(bigCategoryName);
        return ResponseEntity.ok("delete success");
    }



//    @PutMapping("/update-small-category")
//    public ResponseEntity<String> updateSmallCategory(
//            @RequestParam String bigCategoryName,
//            @RequestParam String oldSmallCategoryName,
//            @RequestParam String newSmallCategoryName) {
//
//        bookService.updateSmallCategory(bigCategoryName, oldSmallCategoryName, newSmallCategoryName);
//        return ResponseEntity.ok("Small category updated successfully.");
//    }








}

