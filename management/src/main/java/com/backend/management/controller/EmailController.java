package com.backend.management.controller;

import com.backend.management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;


    // API để gọi hàm gửi email nhắc nhở dueDate
    @GetMapping("/send-due-date-reminders")
    public ResponseEntity<String> sendDueDateReminderBooksManually() {
        try {
            emailService.sendDueDateReminderBooks();
            return ResponseEntity.ok("Đã gửi email nhắc nhở thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi gửi email nhắc nhở: " + e.getMessage());
        }
    }
}
