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

    public Post updatePost(String id, Post postDetails){
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("POST NOT FOUND WITH ID" +id));

        post.setTitle(postDetails.getTitle());
        post.setAuthor(postDetails.getAuthor());
        post.setContent(postDetails.getContent());
        post.setStatus(postDetails.getStatus());
        post.setImg(postDetails.getImg());

        return postRepo.save(post);
    }


    public void deletePost(String id){
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("not found with id"+ id));
        postRepo.delete(post);
    }



}
