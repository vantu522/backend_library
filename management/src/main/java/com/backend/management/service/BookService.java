package com.backend.management.service;

import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.model.CategoryCount;
import com.backend.management.model.PaginatedResponse;
import com.backend.management.repository.BookRepo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    // lay tat ca cac sach
    public Page<Book> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepo.findAll(pageable);
    }

    // lay sach theo id
    public Optional<Book> getBookByBookId(String bookId) {
        return bookRepo.findByBookId(bookId);
    }

    // them sach
    public Book addBook(Book book) {
        return bookRepo.save(book);
    }

    // lay sach theo id va cap nhat sach
    public Book updateBook(String bookId, Book updatedBook) {
        Book existingBook = bookRepo.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("book not found with id" + bookId));
        if (updatedBook.getBookId() != null) {
            existingBook.setBookId(updatedBook.getBookId());
        }
        if (updatedBook.getTitle() != null) {
            existingBook.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getAuthor() != null) {
            existingBook.setAuthor(updatedBook.getAuthor());
        }
        if (updatedBook.getDescription() != null) {
            existingBook.setDescription(updatedBook.getDescription());
        }
        if (updatedBook.getPublicationYear() != null) {
            existingBook.setPublicationYear(updatedBook.getPublicationYear());
        }
        if (updatedBook.getBigCategory() != null) {
            existingBook.setCategory(updatedBook.getBigCategory());
        }
        if (updatedBook.getQuantity() != null) {
            existingBook.setQuantity(updatedBook.getQuantity());
        }
        if (updatedBook.getAvailability() != null) {
            existingBook.setAvailability(updatedBook.getAvailability());
        }
        if (updatedBook.getNxb() != null) {
            existingBook.setNxb(updatedBook.getNxb());
        }

        return bookRepo.save(existingBook);

    }

    // xoa sach theo id
    public void deleteBook(String bookId) {
        bookRepo.deleteById(bookId);
    }


    // cau hinh slug
    private String toSlug(String input) {
        if (input == null)
            return "";

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("/", " ")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    // kiem tra sach co san hay hoc
    public boolean isBookAvailable(String bookId) {
        Optional<Book> book = bookRepo.findByBookId(bookId);
        return book.map(Book::getAvailability).orElse(false);
    }

    // dem tong so sach dang co
    public int getTotalBooksInStock() {
        List<Book> books = bookRepo.findAllQuantities();
        return books.stream()
                .mapToInt(Book::getQuantity)
                .sum();
    }

    // dem so sach cua moi the loai
    public Map<String, Long> getCategoryDistribution() {
        List<CategoryCount> results = bookRepo.getCategoryDistribution();
        Map<String, Long> categoryCount = new HashMap<>();

        for (CategoryCount result : results) {
            if (result.getId() != null) {
                categoryCount.put(result.getId(), result.getCount());
            }
        }

        return categoryCount;
    }

    // lay sach theo ten hoac tac gia
    public List<Book> searchBooks(String title, String author) {
        String titleSlug = title != null ? toSlug(title) : null;
        String authorSlug = author != null ? toSlug(author) : null;
        if (titleSlug != null && authorSlug != null) {
            return bookRepo.findAll().stream()
                    .filter(book -> toSlug(book.getTitle()).equals(titleSlug)
                            && book.getAuthor().stream().anyMatch(a -> toSlug(a).equals(authorSlug)))
                    .collect(Collectors.toList());
        } else if (author != null) {
            return bookRepo.findAll().stream()
                    .filter(book -> book.getAuthor().stream().anyMatch(a -> toSlug(a).equals(authorSlug)))
                    .collect(Collectors.toList());
        } else if (title != null) {
            return bookRepo.findAll().stream()
                    .filter(book -> toSlug(book.getTitle()).equals(titleSlug))
                    .collect((Collectors.toList()));
        }
        return bookRepo.findAll();
    }

}
