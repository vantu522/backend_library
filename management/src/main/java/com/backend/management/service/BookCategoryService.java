package com.backend.management.service;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCategoryService {
    @Autowired
    private BookRepo bookRepo;

    public List<BookCategory> getAllBigCategories() {
        // lay het cac sach
        List<Book> books = bookRepo.findAll();

        List<BookCategory> bigCategories = books.stream()
                .flatMap(book -> book.getBigCategory().stream())
                .distinct()
                .collect(Collectors.toList());
        return bigCategories;
    }
}
