package com.backend.management.controller;


import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Librarian;
import com.backend.management.model.LoginRequest;
import com.backend.management.service.LibrarianService;
import jakarta.mail.MessagingException;
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


    // dang nhap
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Librarian librarian = librarianService.authenticateLibrarian(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(librarian);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    // them thong tin librarians
    @PostMapping("/register")
    public ResponseEntity<Librarian> registerLibrarian( @RequestBody Librarian librarian) {
        Librarian savedLibrarian = librarianService.addLibrarian(librarian);
        return ResponseEntity.ok(savedLibrarian);
    }

    // gui mail
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendPasswordResetOtp(@RequestBody LoginRequest request) throws MessagingException {
        try{
            librarianService.sendPasswordResetOtp(request.getUsername());
            return ResponseEntity.ok("da gui otp thanh cong");
        } catch(Exception e){
            return ResponseEntity.badRequest().body("khong the gui otp:"+ e.getMessage());
        }
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
