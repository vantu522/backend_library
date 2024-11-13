package com.backend.management.controller;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.service.BookCategoryService;
import com.backend.management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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


    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String author)
    {
        return bookService.searchBooks(name,author); // Gọi phương thức trong BookService
    }

    // lay ca the loai lon
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




}