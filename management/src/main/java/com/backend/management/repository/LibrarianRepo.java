package com.backend.management.repository;

import com.backend.management.model.Librarian;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibrarianRepo extends MongoRepository<Librarian, String> {
    Optional<Librarian> findByUsername(String username);
}
