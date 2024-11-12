package com.backend.management.controller;


import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Librarian;
import com.backend.management.model.LoginRequest;
import com.backend.management.service.LibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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




}
