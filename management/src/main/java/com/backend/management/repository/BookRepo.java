package com.backend.management.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.model.CategoryCount;

@Repository
public interface BookRepo extends MongoRepository<Book, String>  {
    Optional<Book> findByBookId(String bookId);

    Book findByTitle (String title);

    @Query(value = "{}", fields = "{quantity: 1}")
    List<Book> findAllQuantities();

    @Aggregation(pipeline = {
            "{ $unwind: '$bigCategory' }",
            "{ $group: { _id: '$bigCategory.name', count: { $sum: 1 } } }"
    })
    List<CategoryCount> getCategoryDistribution();

    @Aggregation(pipeline = {
            "{ $unwind: '$bigCategory' }",
            "{ $group: { _id: '$bigCategory.name' } }",
            "{ $project: { _id: 0, name: '$_id' } }"
    })
    List<String> findDistinctBigCategories();

    @Query(value = "{ 'bigCategory': { $elemMatch: { 'name': ?0 } } }",
            fields = "{ 'bigCategory.$': 1 }")
    List<Book> findByBigCategoryName(String bigCategoryName);

    List<String> findByLikedByMembersContains(String memberId);

    Book findByTitleAndAuthorAndPublicationYear(String title, List<String> author, Integer publicationYear);
}