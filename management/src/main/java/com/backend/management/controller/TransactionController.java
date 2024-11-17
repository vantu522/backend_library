package com.backend.management.controller;

import com.backend.management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody String memberId, @RequestBody String bookId) {
        String result = transactionService.borrowBook(memberId, bookId);
        return ResponseEntity.ok(result);
    }

    // API trả sách
    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestBody String memberId, @RequestBody String bookId) {
        String result = transactionService.returnBook(memberId, bookId);
        return ResponseEntity.ok(result);
    }

    // API gia hạn sách
    @PostMapping("/renew")
    public ResponseEntity<String> renewBook(@RequestBody String memberId, @RequestBody String bookId) {
        String result = transactionService.renewBook(memberId, bookId);
        return ResponseEntity.ok(result);
    }
}
