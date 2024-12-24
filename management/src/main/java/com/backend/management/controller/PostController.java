package com.backend.management.controller;

import com.backend.management.exception.EntityNotFoundException;
import com.backend.management.exception.ImageValidationException;
import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    // tao bai viet moi
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestPart("post") String postJson,
            @RequestPart(value = "image", required = false) MultipartFile img) {
        try {
            // Chuyển JSON sang HashMap
            Gson gson = new Gson();
            Map<String, Object> postMap = gson.fromJson(postJson, Map.class);

            // Gán các giá trị cho đối tượng Post
            Post post = new Post();
            post.setTitle((String) postMap.get("title"));
            post.setContent((String) postMap.get("content"));
            post.setAuthor((String) postMap.get("author"));
            post.setCreatedAt(LocalDateTime.now()); // Thiết lập thời gian hiện tại
            post.setImg("");
            post.setStatus((String) postMap.get("status"));

            // Gọi service để xử lý tạo bài viết
            Post createdPost = postService.createPost(post, img);
            return ResponseEntity.ok(createdPost);
        } catch (ImageValidationException e) {
            return ResponseEntity.badRequest().build(); // Lỗi ảnh không hợp lệ
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Lỗi hệ thống
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> updatePost(
            @PathVariable String id,
            @RequestPart("post") String postJson,
            @RequestPart(value = "image", required = false) MultipartFile img) {
        try {
            Gson gson = new Gson();

            // Chuyển JSON sang HashMap
            Map<String, Object> postMap = gson.fromJson(postJson, Map.class);

            // Gán các giá trị cho đối tượng Book
            Post updatedPost = new Post();
            updatedPost.setTitle((String) postMap.get("title"));
            updatedPost.setContent((String) postMap.get("content"));
            updatedPost.setAuthor((String) postMap.get("author")); // Với danh sách String
            updatedPost.setStatus((String) postMap.get("status"));


            // Cập nhật book thông qua bookService
            Post updatePost = postService.updatePost(id, updatedPost, img);
            return ResponseEntity.ok(updatedPost);
        } catch (ImageValidationException e) {
            return ResponseEntity.badRequest().build(); // Lỗi ảnh không hợp lệ
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Lỗi hệ thống
        }
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Xóa thành công");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi!");
        }
    }
    @GetMapping
    public List<Post> getAllPosts(){
        return postService.getPublicPosts();
    }

}
