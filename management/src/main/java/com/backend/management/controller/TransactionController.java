package com.backend.management.controller;

import com.backend.management.model.TransactionHistory;
import com.backend.management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    // dem so sach dang muon
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


    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody Map<String, String> requestBody) {
        // Kiểm tra dữ liệu đầu vào
        String name = requestBody.get("name");
        String title = requestBody.get("title");
        String phoneNumber = requestBody.get("phoneNumber");

//        if (name == null || name.isEmpty() || title == null || title.isEmpty() ) {
//            return ResponseEntity.badRequest().body("Thiếu thông tin cần thiết: name, title, memberId, bookId");
//        }

        try {
            // Gọi service để mượn sách
            String result = transactionService.borrowBook(name, title, phoneNumber);

            // Trả kết quả lại cho client
            return ResponseEntity.ok(result);
        } catch (Exception e) {
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

//        if (memberId == null || memberId.isEmpty() || bookId == null || bookId.isEmpty()) {
//            return ResponseEntity.badRequest().body("Thiếu thông tin: memberId, bookId");
//        }

        try {
            // Gọi service để trả sách
            String result = transactionService.returnBook(name, title,phoneNumber);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi trả sách: " + e.getMessage());
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

    @GetMapping("/weekly-stats")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyStats() {
        List<Map<String, Object>> stats = transactionService.getWeeklyStats();
        return ResponseEntity.ok(stats);
    }
    @GetMapping ("/statistics")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyStatistics(
            @RequestBody Map<String, String> requestBody
    ) {
        // Lấy giá trị "transactionType" từ requestBody
        String transactionType = requestBody.get("transactionType");

        if (transactionType == null || transactionType.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Phản hồi lỗi nếu thiếu transactionType
        }

        // Gọi service để lấy thống kê
        List<Map<String, Object>> statistics = transactionService.getMonthlyStatistics(transactionType);

        return ResponseEntity.ok(statistics);
    }

}