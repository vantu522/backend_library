package com.backend.management.controller;

import com.backend.management.model.Post;
import com.backend.management.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    // tao bai viet moi
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    @GetMapping
    public List<Post> getAllPosts(){
        return postService.getPublicPosts();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody Post postDetails){
        Post updatedPost = postService.updatePost(id, postDetails);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id){
        postService.deletePost(id);
        return ResponseEntity.ok(" Xóa thành công");
    }


}
