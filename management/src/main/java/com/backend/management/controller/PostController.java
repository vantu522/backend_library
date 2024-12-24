package com.backend.management.controller;

import com.backend.management.exception.ImageValidationException;
import com.backend.management.model.Post;
import com.backend.management.service.PostService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    // tao bai viet moi
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestPart("post") String postJson,
            @RequestPart(value = "img", required = false) MultipartFile img) {
        try {
            // Chuyển JSON sang đối tượng Post
            Gson gson = new Gson();
            Post postDetails = gson.fromJson(postJson, Post.class);

            // Gọi service để xử lý tạo bài viết
            Post createdPost = postService.createPost(postDetails, img);
            return ResponseEntity.ok(createdPost);
        } catch (ImageValidationException e) {
            return ResponseEntity.badRequest().body(null); // Lỗi ảnh không hợp lệ
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Lỗi hệ thống
        }
    }


    @GetMapping
    public List<Post> getAllPosts(){
        return postService.getPublicPosts();
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> updatePost(
            @PathVariable String id,
            @RequestPart("post") String postJson,
            @RequestPart(value = "img", required = false) MultipartFile img) {
        try {
            // Chuyển đổi JSON sang đối tượng Post
            Gson gson = new Gson();
            Post postDetails = gson.fromJson(postJson, Post.class);

            // Gọi service để cập nhật Post
            Post updatedPost = postService.updatePost(id, postDetails, img);
            return ResponseEntity.ok(updatedPost);
        } catch (ImageValidationException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id){
        postService.deletePost(id);
        return ResponseEntity.ok(" Xóa thành công");
    }


}
