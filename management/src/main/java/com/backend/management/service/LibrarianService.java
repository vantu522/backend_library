package com.backend.management.service;

import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Librarian;
import com.backend.management.repository.LibrarianRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibrarianService {
    @Autowired
    private LibrarianRepo librarianRepo;


    public Librarian getLibrarianByUsername(String username){
        return librarianRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    public Librarian authenticateLibrarian(String username, String password){
        Librarian librarian = getLibrarianByUsername(username);
        if(librarian.matchPassword(password)){
            return librarian;
        } else{
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

   public Librarian addLibrarian(Librarian librarian){
        return librarianRepo.save(librarian);
   }

}
