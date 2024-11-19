package com.backend.management.controller;

import com.backend.management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody Map<String, String> requestBody) {
        // Kiểm tra dữ liệu đầu vào
        String name = requestBody.get("name");
        String title = requestBody.get("title");
        String memberId = requestBody.get("memberId");
        String bookId = requestBody.get("bookId");

//        if (name == null || name.isEmpty() || title == null || title.isEmpty() ) {
//            return ResponseEntity.badRequest().body("Thiếu thông tin cần thiết: name, title, memberId, bookId");
//        }

        try {
            // Gọi service để mượn sách
            String result = transactionService.borrowBook(name, title, memberId, bookId);

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
        String memberId = requestBody.get("memberId");
        String bookId = requestBody.get("bookId");

//        if (memberId == null || memberId.isEmpty() || bookId == null || bookId.isEmpty()) {
//            return ResponseEntity.badRequest().body("Thiếu thông tin: memberId, bookId");
//        }

        try {
            // Gọi service để trả sách
            String result = transactionService.returnBook(name, title, memberId, bookId);
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
        String memberId = requestBody.get("memberId");
        String bookId = requestBody.get("bookId");

        try {
            String result = transactionService.renewBook(name, title, memberId, bookId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi trả sách: " + e.getMessage());
        }
    }
}