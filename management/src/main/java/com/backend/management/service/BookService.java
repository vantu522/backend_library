package com.backend.management.service;

import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.model.CategoryCount;
import com.backend.management.model.PaginatedResponse;
import com.backend.management.repository.BookRepo;
import com.backend.management.utils.SlugUtil;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;


import org.springframework.data.mongodb.core.query.UpdateDefinition;
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

    public List<Book> searchBooks(String title, String author) {
        Criteria criteria = new Criteria();

        if (title != null) {
            criteria.and("title").regex(".*" + title + ".*", "i");
        }

        if (author != null) {
            criteria.and("author").regex(".*" + author + ".*", "i");
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Book.class);
    }


    public List<Book> suggestBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Criteria criteria = new Criteria().orOperator(
                Criteria.where("title").regex("^" + query, "i"),
                Criteria.where("author").regex("^" + query, "i")
        );

        Query querys = new Query(criteria).limit(10);
        return mongoTemplate.find(querys, Book.class);
    }

    public List<Book> getBooksBySubCategory(String subCategoryName, String bigCategoryName){
        String subSlug = SlugUtil.toSlug(subCategoryName);
        String bigSlug = SlugUtil.toSlug(bigCategoryName);
        return bookRepo.findAll().stream()
                .filter(book -> book.getBigCategory().stream()
                        .anyMatch(bigCategory -> bigCategory.getSmallCategory().stream()
                                .anyMatch(smallCategoty -> SlugUtil.toSlug(smallCategoty).equals(subSlug))))
                .collect(Collectors.toList());
    }

    // chinh sua ten the loai lon
    public void updateBigCategoryName(String oldName, String newName){

        Query query = new Query(Criteria.where("bigCategory.name").is(oldName));

        Update update = new Update().set("bigCategory.$.name", newName);

        // Thực hiện cập nhật trên tất cả sách phù hợp
        mongoTemplate.updateMulti(query, update, Book.class);
    }

    // xoa ten the loai lon
    public void deleteBigCategoryName(String bigCategoryName){
        Query query = new Query(Criteria.where("bigCategory.name").is(bigCategoryName));
        mongoTemplate.remove(query,Book.class);
    }

    public void updateSmallCategory(String bigCategoryName, String oldSmallCategoryName, String newSmallCategoryName) {
        // Tạo truy vấn tìm các document phù hợp
        Query query = new Query(Criteria.where("bigCategory")
                .elemMatch(Criteria.where("name").is(bigCategoryName)
                        .and("smallCategory").is(oldSmallCategoryName)));

        // Tạo update để thay đổi giá trị
        Update update = new Update()
                .set("bigCategory.$[elem].smallCategory.$[subelem]", newSmallCategoryName);

        // Thực hiện update nhiều document với collection và options cụ thể
        mongoTemplate.updateMulti(
                query,
                update,
                Book.class,
                mongoTemplate.getCollectionName(Book.class)
        );
    }

}
