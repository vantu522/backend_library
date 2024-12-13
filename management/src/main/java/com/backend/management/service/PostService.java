package com.backend.management.service;

import com.backend.management.model.Post;
import com.backend.management.repository.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepo postRepo;

    public Post createPost(Post post){
        post.setCreatedAt(LocalDateTime.now());
        return postRepo.save(post);
    }

    public List<Post> getPublicPosts() {
        return postRepo.findByStatus("công khai"); // Trả về các bài viết công khai
    }


}
