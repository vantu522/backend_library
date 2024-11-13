package com.backend.management.controller;


import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Librarian;
import com.backend.management.model.LoginRequest;
import com.backend.management.service.LibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.VariableOperators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/librarians")
public class LibrarianController {
    @Autowired
    private LibrarianService librarianService;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Librarian librarian = librarianService.authenticateLibrarian(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(librarian);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Librarian> registerLibrarian( @RequestBody Librarian librarian) {
        Librarian savedLibrarian = librarianService.addLibrarian(librarian);
        return ResponseEntity.ok(savedLibrarian);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody LoginRequest request) {
        librarianService.sendPasswordResetOtp(request.getUsername());
        return ResponseEntity.ok().body("OTP đã được gửi đến email của bạn");
    }

    // Reset mật khẩu bằng OTP
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        librarianService.resetPasswordWithOtp(
                request.get("username"),
                request.get("otp"),
                request.get("newPassword")
        );
        return ResponseEntity.ok().body("Mật khẩu đã được đổi thành công");
    }

    // Đổi mật khẩu (khi đã đăng nhập)
    @PostMapping("/change")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        librarianService.changePassword(
                request.get("username"),
                request.get("oldPassword"),
                request.get("newPassword")
        );
        return ResponseEntity.ok().body("Mật khẩu đã được đổi thành công");
    }





}
