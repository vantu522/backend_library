package com.backend.management.repository;

import com.backend.management.model.TransactionHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TransactionHistoryRepo extends MongoRepository<TransactionHistory, String> {

    // Tìm tất cả các giao dịch của một thành viên cụ thể
    List<TransactionHistory> findByMemberId(String memberId);

    // Tìm tất cả các giao dịch theo loại giao dịch (mượn, trả, gia hạn, quá hạn)
    List<TransactionHistory> findByTransactionType(String transactionType);

    // Tìm tất cả các giao dịch của một cuốn sách cụ thể
    List<TransactionHistory> findByBookId(String bookId);
}
