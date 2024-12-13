package com.backend.management.repository;

import com.backend.management.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepo extends MongoRepository<Post,String> {
    List<Post> findByAuthor(String author);
    List<Post> findByIsPublishedTrue();
}
