package com.backend.management.service;

import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Librarian;
import com.backend.management.repository.LibrarianRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LibrarianService {
    @Autowired
    private LibrarianRepo librarianRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // lay thong tin thu thu dua tren username
    public Librarian getLibrarianByUsername(String username){
        return librarianRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    //check xem mat khau user nhap vao co trung voi ma hoa
    public Librarian authenticateLibrarian(String username, String password) {
        Librarian librarian = getLibrarianByUsername(username);
        if (passwordEncoder.matches(password, librarian.getPassword())) {
            return librarian;
        } else {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    // them thong tin thu thu
    public Librarian addLibrarian(Librarian librarian){
        String encodedPassword = passwordEncoder.encode(librarian.getPassword());
        librarian.setPassword(encodedPassword);

        return librarianRepo.save(librarian);
   }

   //





}
