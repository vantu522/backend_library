package com.backend.management.service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.backend.management.repository.BookRepo;
import com.backend.management.utils.SlugUtil;
import com.backend.management.model.Book;
import com.backend.management.model.CategoryCount;
import com.backend.management.model.PaginatedResponse;
import com.backend.management.exception.BookServiceException;
import com.backend.management.exception.ResourceNotFoundException;

@Service
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<Book> getAllBooks(int page, int size, String sortBy) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page and size must be non-negative");
        }

        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
            Pageable pageable = PageRequest.of(page, size, sort);
            return bookRepo.findAll(pageable);
        } catch (Exception e) {
            logger.error("Error fetching books: {}", e.getMessage(), e);
            throw new BookServiceException("Error fetching books");
        }
    }

    public Book updateBook(String bookId, Book updatedBook) {
        try {
            logger.info("Attempting to update book with ID: {}", bookId);
            logger.info("Update request data: {}", updatedBook);

            Book existingBook = bookRepo.findByBookId(bookId)
                    .orElseThrow(() -> new BookServiceException("book not found with id: " + bookId));

            if (updatedBook.getTitle() != null) existingBook.setTitle(updatedBook.getTitle());
            if (updatedBook.getAuthor() != null) existingBook.setAuthor(updatedBook.getAuthor());
            if (updatedBook.getDescription() != null) existingBook.setDescription(updatedBook.getDescription());
            if (updatedBook.getPublicationYear() != null) existingBook.setPublicationYear(updatedBook.getPublicationYear());
            if (updatedBook.getBigCategory() != null) existingBook.setBigCategory(updatedBook.getBigCategory());
            if (updatedBook.getQuantity() != null) existingBook.setQuantity(updatedBook.getQuantity());
            if (updatedBook.getAvailability() != null) existingBook.setAvailability(updatedBook.getAvailability());
            if (updatedBook.getNxb() != null) existingBook.setNxb(updatedBook.getNxb());
            if (updatedBook.getLikedByMembers() != null) existingBook.setLikedByMembers(updatedBook.getLikedByMembers());
            if (updatedBook.getPageCount() != null) existingBook.setPageCount(updatedBook.getPageCount());
            if (updatedBook.getImg() != null) existingBook.setImg(updatedBook.getImg());

            Book savedBook = bookRepo.save(existingBook);
            logger.info("Book with id {} updated successfully", bookId);

            return savedBook;
        } catch (Exception e) {
            logger.error("Error updating book with ID: {}", bookId);
            throw new BookServiceException("Error updating book with ID: " + bookId);
        }
    }

    public Book addBook(Book book) {
        try {
            if (book == null) {
                logger.warn("Attemp to add null book");
                throw new IllegalArgumentException("Book can not be null");
            }

            if (book.getTitle() == null || book.getTitle().isEmpty()) {
                logger.warn("Book title is missing");
                throw new IllegalArgumentException("Book title is required");
            }

            Book existingBook = bookRepo.findByTitleAndAuthorAndPublicationYear(
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublicationYear()
            );

            if (existingBook != null) {
                existingBook.setQuantity(existingBook.getQuantity() + 1);
                bookRepo.save(existingBook);
                logger.info("Book with title: {} already exists. Increased quantity by 1.", existingBook.getTitle());
                return existingBook;
            }

            logger.info("Adding book with title: {}", book.getTitle());
            Date now = new Date();
            book.setCreatedDate(now);
            Book savedBook = bookRepo.save(book);
            logger.info("Book with title: {} add successfully. Book id: {}", savedBook.getTitle(), savedBook.getBookId());
            return savedBook;
        } catch (Exception Ex) {
            logger.error("Error occurred while adding book: {}", Ex.getMessage());
            throw new BookServiceException("Error adding book with title: {}" + book.getTitle());
        }
    }

    public Optional<Book> getBookByBookId(String bookId) {
        return bookRepo.findByBookId(bookId);
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

    public List<Book> getBooksBySubCategory(String subCategoryName, String bigCategoryName) {
        String subSlug = SlugUtil.toSlug(subCategoryName);
        String bigSlug = SlugUtil.toSlug(bigCategoryName);

        return bookRepo.findAll().stream()
                .filter(book -> book.getBigCategory().stream()
                        .anyMatch(bigCategory -> {
                            String currentBigSlug = SlugUtil.toSlug(bigCategory.getName());
                            if (!currentBigSlug.equals(bigSlug)) {
                                return false;
                            }

                            return bigCategory.getSmallCategory().stream()
                                    .anyMatch(smallCategory -> {
                                        String[] subCategories = smallCategory.split("/");

                                        // Chuyển từng phần tử thành slug và so sánh
                                        for (String subCategory : subCategories) {
                                            String currentSubSlug = SlugUtil.toSlug(subCategory.trim());
                                            // So sánh một phần của slug
                                            if (subSlug.contains(currentSubSlug) || currentSubSlug.contains(subSlug)) {
                                                return true;
                                            }
                                        }
                                        return false;
                                    });
                        }))
                .collect(Collectors.toList());
    }



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

//    public void updateSmallCategory(String bigCategoryName, String oldSmallCategoryName, String newSmallCategoryName) {
//        // Tạo truy vấn tìm các document phù hợp
//        Query query = new Query(Criteria.where("bigCategory")
//                .elemMatch(Criteria.where("name").is(bigCategoryName)
//                        .and("smallCategory").is(oldSmallCategoryName)));
//
//        // Tạo update để thay đổi giá trị
//        Update update = new Update()
//                .set("bigCategory.$[elem].smallCategory.$[subelem]", newSmallCategoryName);
//
//        // Thực hiện update nhiều document với collection và options cụ thể
//        mongoTemplate.updateMulti(
//                query,
//                update,
//                Book.class,
//                mongoTemplate.getCollectionName(Book.class)
//        );
//    }

}
