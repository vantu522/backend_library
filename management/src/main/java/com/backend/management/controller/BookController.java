package com.backend.management.controller;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.model.PaginatedResponse;
import com.backend.management.service.BookCategoryService;
import com.backend.management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private BookCategoryService bookCategoryService;

    // lay tat ca cac sach
    @GetMapping
    public ResponseEntity<PaginatedResponse<Book>> geAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Book> pageBooks = bookService.getAllBooks(page, size);
            return ResponseEntity.ok(PaginatedResponse.of(pageBooks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }







    //lay sach theo id
    @GetMapping("/{bookId}")
    public Optional<Book> getBookByBookId(@PathVariable String bookId) {
        return bookService.getBookByBookId(bookId);
    }

    //them sach
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    //cap nhat sach theo id
    @PutMapping("/update/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable String bookId, @RequestBody Book updatedBook) {
        Book book = bookService.updateBook(bookId,updatedBook);
        return ResponseEntity.ok(book);

    }

    // xoa sach theo id
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




//    @GetMapping("/categories/{bigCategorySlug}/{subCategorySlug}/books")
//    public ResponseEntity<?> getBooksByCategory(
//            @PathVariable String bigCategorySlug,
//            @PathVariable String subCategorySlug,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        try {
//            if (page < 0 || size <= 0) {
//                return ResponseEntity.badRequest()
//                    .body("Page và size phải là số dương");
//            }
//
//            PaginatedResponse<Book> response = bookService.getBooksBySubCategory(
//                    bigCategorySlug,
//                    subCategorySlug,
//                    page,
//                    size
//            );
//
//            return ResponseEntity.ok(response);
//
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(e.getMessage());
//        }
//    }




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
        return bookService.searchBooks(title,author); // Gọi phương thức trong BookService
    }




}

