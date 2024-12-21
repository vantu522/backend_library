package com.backend.management.repository;

import com.backend.management.model.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ContactRepo extends MongoRepository<Contact, String> {
    List<Contact> findByStatusOrderByCreatedAtDesc(String status);
    List<Contact> findByEmailOrderByCreatedAtDesc(String email);
}