package com.backend.management.repository;

import com.backend.management.model.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepo extends MongoRepository<Post,String> {
    List<Post> findByAuthor(String author);
    List<Post> findByStatus(String status, Sort sort);
    Optional<Post> findById(String id);
}
