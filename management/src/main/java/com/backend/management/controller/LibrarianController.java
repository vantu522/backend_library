package com.backend.management.controller;


import com.backend.management.model.Librarian;
import com.backend.management.model.LoginRequest;
import com.backend.management.service.LibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/librarians")
public class LibrarianController {
    @Autowired
    private LibrarianService librarianService;


    @PostMapping("/login")
    public Librarian login(@RequestBody LoginRequest request){
        return librarianService.authenticateLibrarian(request.getUsername(), request.getPassword());
    }
}
