package com.backend.management.repository;

import org.bson.Document;

import java.util.List;

public interface CustomTransactionHistoryRepo {
    List<Document> findTop10MostBorrowedBooks();
}
