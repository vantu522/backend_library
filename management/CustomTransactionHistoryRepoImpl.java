package com.backend.management.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.bson.Document;

import java.util.List;

@Repository
public class CustomTransactionHistoryRepoImpl implements CustomTransactionHistoryRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Document> findTop10MostBorrowedBooks() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("transactionType").is("Mượn")), // Lọc giao dịch mượn
                Aggregation.group("bookId") // Nhóm theo bookId
                        .count().as("borrowCount") // Đếm số lần mượn
                        .first("title").as("title") // Lấy tên sách
                        .first("author").as("author") // Lấy tác giả
                        .first("img").as("img"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "borrowCount")), // Sắp xếp giảm dần
                Aggregation.limit(10) // Giới hạn kết quả top 10
        );

        // Chỉ truyền vào "transactionHistory" và Document.class
        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, "transactionHistory", Document.class
        );

        return results.getMappedResults();
    }
}
