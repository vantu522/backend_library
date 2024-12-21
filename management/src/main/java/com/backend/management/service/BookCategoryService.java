package com.backend.management.service;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.repository.BookRepo;
import com.backend.management.utils.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookCategoryService {
    @Autowired
    private BookRepo bookRepo;


    // lay ra cac the loai lon
    public List<String> getAllBigCategories() {
        return bookRepo.findDistinctBigCategories();
    }




    // Lấy các thể loại nhỏ theo slug của thể loại lớn
    public List<String> getSmallCategories(String bigCategorySlug) {
        // Tìm tất cả các thể loại lớn
        List<String> allCategories = getAllBigCategories();
        // Tìm thể loại lớn tương ứng với slug
        String matchedCategory = allCategories.stream()
                .filter(category -> SlugUtil.toSlug(category).equals(bigCategorySlug))
                .findFirst()
                .orElse(null);
        if (matchedCategory == null) {
            return List.of();
        }

        // Lấy các sách theo thể loại lớn và thu thập các thể loại nhỏ
        return bookRepo.findByBigCategoryName(matchedCategory).stream()
                .flatMap(book -> book.getBigCategory().stream())
                .filter(category -> category.getName().equals(matchedCategory))
                .flatMap(category -> category.getSmallCategory().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }





}
