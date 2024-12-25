package com.backend.management.controller;

import com.backend.management.exception.BookUnavailableException;
import com.backend.management.exception.InvalidRequestException;
import com.backend.management.model.TransactionHistory;
import com.backend.management.repository.TransactionHistoryRepo;
import com.backend.management.service.TransactionService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.DocumentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionHistoryRepo transactionHistoryRepo;


//     dem so sach dang muon
    @GetMapping("/count-borrowed")
    public ResponseEntity<Long> getBorrowedBooksCount() {
        long count = transactionService.countBorrowedBooks();
        return ResponseEntity.ok(count);
    }

   @GetMapping("/count-returned")
   public ResponseEntity<Long> getReturnedBooksCount(){
        long count = transactionService.countReturnedBooks();
        return ResponseEntity.ok(count);
   }

    @GetMapping("/borrowed")
    public ResponseEntity<List<Map<String, String>>> getBookBorowed() {
        List<Map<String, String>> transactions = transactionService.getAllBorrowTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/returned")
    public ResponseEntity<List<Map<String, String>>> getBookReturn() {
        List<Map<String, String>> transactions = transactionService.getAllReturnTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/renewed")
    public ResponseEntity<List<Map<String, String>>> getBookRenew() {
        List<Map<String, String>> transactions = transactionService.getAllRenewTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, String>>> getBookPending() {
        List<Map<String, String>> transactions = transactionService.getAllPendingTransactions();
        return ResponseEntity.ok(transactions);
    }


    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody Map<String, String> requestBody) {
        // Kiểm tra dữ liệu đầu vào
        String name = requestBody.get("name");
        String title = requestBody.get("title");
        String phoneNumber = requestBody.get("phoneNumber");
        String borrowDateStr = requestBody.get("borrowDateStr");

//        if (name == null || name.isEmpty() || title == null || title.isEmpty() ) {
//            return ResponseEntity.badRequest().body("Thiếu thông tin cần thiết: name, title, memberId, bookId");
//        }

        try {
            // Gọi service để mượn sách
            String result = transactionService.borrowBook(name, title, phoneNumber, borrowDateStr);

            // Trả kết quả lại cho client
            return ResponseEntity.ok(result);
        }catch (InvalidRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (BookUnavailableException e) {
            // Xử lý trường hợp không còn sách
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Không còn sách để mượn.");
        }
        catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi mượn sách: " + e.getMessage());
        }
    }


    // API trả sách
    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestBody Map<String, String> requestBody) {
        // Lấy thông tin từ request body
        String name = requestBody.get("name");
        String title = requestBody.get("title");
        String phoneNumber = requestBody.get("phoneNumber");

        try {
            // Gọi service để trả sách
            String result = transactionService.returnBook(name, title,phoneNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("ratingLink", "/ratings/submit");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw e;
        }
    }

    // API gia hạn sách
    @PostMapping("/renew")
    public ResponseEntity<String> renewBook(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        String title = requestBody.get("title");
        String phoneNumber = requestBody.get("phoneNumber");

        try {
            String result = transactionService.renewBook(name, title, phoneNumber);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi trả sách: " + e.getMessage());
        }

    }
    @PostMapping("/approve")
    public String approveRequest(@RequestBody Map<String, Object> requestBody) {
        String name = (String) requestBody.get("name");
        String title = (String) requestBody.get("title");
        String phoneNumber = (String) requestBody.get("phoneNumber");
        boolean isAprove = (boolean) requestBody.get("isAprove");

        return transactionService.approveRequest(name, title, phoneNumber, isAprove);
    }

    @GetMapping("/weekly-stats")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyStats() {
        List<Map<String, Object>> stats = transactionService.getWeeklyStats();
        return ResponseEntity.ok(stats);
    }
    @GetMapping ("/statistics")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyStatistics() {
        try {

            List<Map<String, Object>> statistics = transactionService.getMonthlyStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/topBorrow")
    public List<Document> getTop10BorrowBook() {
        return transactionHistoryRepo.findTop10MostBorrowedBooks();
    }

    @GetMapping("/user")
    public ResponseEntity<List<TransactionHistory>> getUserTransactionHistory(
            @RequestParam String memberId) {
        List<TransactionHistory> transactionHistory = transactionService.getTransactionHistoryByUser(memberId);
        return ResponseEntity.ok(transactionHistory);
    }
    @DeleteMapping("/delete/{id}")
    public void deletePendingTransactions(String id) {
        transactionService.deletePendingBorrowRequest(id);
    }
}