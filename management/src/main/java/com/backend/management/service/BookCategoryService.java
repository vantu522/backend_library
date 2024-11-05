package com.backend.management.service;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCategoryService {
    @Autowired
    private BookRepo bookRepo;


    public List<BookCategory> getAllBigCategories() {
        // Lấy tất cả sách và phân loại thể loại lớn duy nhất
        List<Book> books = bookRepo.findAll();

        return books.stream()
                .flatMap(book -> book.getBigCategory().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getSmallCategories(String bigCategorySlug) {
        List<Book> books = bookRepo.findAll();



        return books.stream()
                .flatMap(book -> book.getBigCategory().stream())
                .filter(bigCategory -> toSlug(bigCategory.getName()).equals(bigCategorySlug))
                .flatMap(bigCategory -> bigCategory.getSmallCategory().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private String toSlug(String input) {
        if (input == null) return "";

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("/","")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    // Thêm method để lấy một book category theo slug
    public BookCategory getBigCategoryBySlug(String slug) {
        return bookRepo.findAll().stream()
                .flatMap(book -> book.getBigCategory().stream())
                .filter(category -> toSlug(category.getName()).equals(slug))
                .findFirst()
                .orElse(null);
    }
}
