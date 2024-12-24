package com.backend.management.controller;

import com.backend.management.exception.ImageValidationException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.model.PaginatedResponse;
import com.backend.management.service.BookCategoryService;
import com.backend.management.service.BookService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private BookCategoryService bookCategoryService;

    // lay tat ca cac sach
    @GetMapping
    public ResponseEntity<PaginatedResponse<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Book> books = bookService.getAllBooks(page, size);
            PaginatedResponse<Book> response = PaginatedResponse.of(books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/categories/{bigCategoryName}/{subCategoryName}/books")
    public ResponseEntity<List<Book>> getBooksBySubCategory(@PathVariable String subCategoryName,
                                                            @PathVariable String bigCategoryName){
        List<Book> books = bookService.getBooksBySubCategory(subCategoryName, bigCategoryName);
        return ResponseEntity.ok(books);
    }



    //lay sach theo id
    @GetMapping("/{bookId}")
    public Optional<Book> getBookByBookId(@PathVariable String bookId) {
        return bookService.getBookByBookId(bookId);
    }

    //them sach


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> addBook(
            @RequestPart("book") String bookJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Gson gson = new Gson();

            // Chuyển JSON sang HashMap
            Map<String, Object> bookMap = gson.fromJson(bookJson, Map.class);

            // Gán các giá trị cho đối tượng Book
            Book book = new Book();
            book.setTitle((String) bookMap.get("title"));
            book.setDescription((String) bookMap.get("description"));
            book.setAuthor((List<String>) bookMap.get("author")); // Với danh sách String
            book.setPublicationYear(((Number) bookMap.get("publicationYear")).intValue());
            book.setBigCategory((List<BookCategory>) bookMap.get("bigCategory")); // Với danh sách BookCategory
            book.setQuantity(((Number) bookMap.get("quantity")).intValue());
            book.setAvailability((Boolean) bookMap.get("availability"));
            book.setNxb((String) bookMap.get("nxb"));
            book.setLikedByMembers((List<String>) bookMap.get("likedByMembers"));
            book.setPageCount(((Number) bookMap.get("pageCount")).intValue());

            Book savedBook = bookService.addBook(book, image);
            return ResponseEntity.ok(savedBook);
        } catch (ImageValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping(value = "/update/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> updateBook(
            @PathVariable String bookId,
            @RequestPart("book") String bookJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Gson gson = new Gson();

            // Chuyển JSON sang HashMap
            Map<String, Object> bookMap = gson.fromJson(bookJson, Map.class);

            // Gán các giá trị cho đối tượng Book
            Book updatedBook = new Book();
            updatedBook.setTitle((String) bookMap.get("title"));
            updatedBook.setDescription((String) bookMap.get("description"));
            updatedBook.setAuthor((List<String>) bookMap.get("author")); // Với danh sách String
            updatedBook.setPublicationYear(((Number) bookMap.get("publicationYear")).intValue());
            updatedBook.setBigCategory((List<BookCategory>) bookMap.get("bigCategory")); // Với danh sách BookCategory
            updatedBook.setQuantity(((Number) bookMap.get("quantity")).intValue());
            updatedBook.setAvailability((Boolean) bookMap.get("availability"));
            updatedBook.setNxb((String) bookMap.get("nxb"));
            updatedBook.setLikedByMembers((List<String>) bookMap.get("likedByMembers"));
            updatedBook.setPageCount(((Number) bookMap.get("pageCount")).intValue());

            // Cập nhật book thông qua bookService
            Book book = bookService.updateBook(bookId, updatedBook, image);
            return ResponseEntity.ok(book);
        } catch (ImageValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{bookId}/image")
    public ResponseEntity<String> getBookImage(@PathVariable String bookId) {
        try {
            String base64Image = bookService.getBookImage(bookId);
            return ResponseEntity.ok(base64Image);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
