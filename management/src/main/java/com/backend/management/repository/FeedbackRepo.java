package com.backend.management.repository;

import com.backend.management.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackRepo extends MongoRepository<Feedback, String> {
    List<Feedback> findByStatus(String status);
    List<Feedback> findByNameContainingOrEmailContainingOrContentContaining(String name, String email, String content);
}
