package com.backend.management.controller;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.service.BookCategoryService;
import com.backend.management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private BookCategoryService bookCategoryService;

    // lay tat ca cac sach
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(){
        List<Book> books= bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    //lay sach theo id
    @GetMapping("/{idBook}")
    public Optional<Book> getBookById(@PathVariable String idBook) {
        return bookService.getBookById(idBook);
    }

    //them sach
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    //cap nhat sach theo id
    @PutMapping("/update/{idBook}")
    public ResponseEntity<Book> updateBook(@PathVariable String idBook, @RequestBody Book updatedBook) {
        Book book = bookService.updateBook(idBook,updatedBook);
        return ResponseEntity.ok(book);

    }

    // xoa sach theo id
    @DeleteMapping("/delete/{idBook}")
    public void deleteBook(@PathVariable String idBook) {
        bookService.deleteBook(idBook);
    }


    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String author)
    {
        return bookService.searchBooks(name,author); // Gọi phương thức trong BookService
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getBigCategories() {
        List<String> bigCategories = bookCategoryService.getAllBigCategories()
                .stream()
                .map(BookCategory::getName) // Giả định BookCategory có phương thức getName()
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(bigCategories);
    }

    @GetMapping("/categories/{bigCategoryName}")
    public ResponseEntity<List<String>> getSmallCategories(@PathVariable String bigCategoryName) {
        try {
            List<String> smallCategories = bookCategoryService.getSmallCategories(bigCategoryName);
            return ResponseEntity.ok(smallCategories);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }



    @GetMapping("/categories/{bigCategoryName}/{subCategoryName}/books")
    public ResponseEntity<List<Book>> getBooksBySubCategory(@PathVariable String subCategoryName,
                                                            @PathVariable String bigCategoryName){
        List<Book> books = bookService.getBooksBySubCategory(subCategoryName, bigCategoryName);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{idBook}/availability")
    public boolean checkAvaibility(@PathVariable String idBook){
        return bookService.isBookAvailable(idBook);
    }



}