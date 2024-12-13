package com.backend.management.service;

import com.backend.management.model.Post;
import com.backend.management.repository.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class PostService {
    @Autowired
    private PostRepo postRepo;

    public Post createPost(Post post){
        post.setCreatedAt(LocalDateTime.now());
        return postRepo.save(post);
    }

    // Lấy tất cả bài viết
    public List<Post> getAllPosts() {
        return postRepo.findByIsPublishedTrue();
    }


}
