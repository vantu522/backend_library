package com.backend.management.repository;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.model.CategoryCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public interface BookRepo extends MongoRepository<Book, String> {

    Optional<Book> findByBookId(String bookId);

    // dem tat ca sos sasch
    @Query(value = "{}", fields = "{quantity: 1}")
    List<Book> findAllQuantities();

    // Tìm theo tên
    @Query("{ 'name': { $regex: ?0, $options: 'i' }}")
    Page<Book> findByNameRegex(Pattern pattern, Pageable pageable);

    // Tìm theo tác giả
    @Query("{ 'author': { $elemMatch: { $regex: ?0, $options: 'i' }}}")
    Page<Book> findByAuthorRegex(Pattern pattern, Pageable pageable);

    // Tìm theo cả tên và tác giả
    @Query("{ $and: [ " +
            "{ 'name': { $regex: ?0, $options: 'i' }}, " +
            "{ 'author': { $elemMatch: { $regex: ?1, $options: 'i' }}} " +
            "]}")
    Page<Book> findByNameRegexAndAuthorRegex(Pattern namePattern, Pattern authorPattern, Pageable pageable);

    // tinh tong sach moi the loai lonw
    @Aggregation(pipeline = {
            "{ $unwind: '$bigCategory' }",
            "{ $group: { _id: '$bigCategory.name', count: { $sum: 1 } } }"
    })
    List<CategoryCount> getCategoryDistribution();




    //truy van lay cac the loai lon
    @Aggregation(pipeline = {
            "{ $unwind: '$bigCategory' }",
            "{ $group: { _id: '$bigCategory.name' } }",
            "{ $project: { _id: 0, name: '$_id' } }"
    })
    List<String> findDistinctBigCategories();




    // Sửa lại phương thức query small categories
    @Query(value = "{ 'bigCategory': { $elemMatch: { 'name': ?0 } } }",
            fields = "{ 'bigCategory.$': 1 }")
    List<Book> findByBigCategoryName(String bigCategoryName);



}