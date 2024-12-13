package com.backend.management.controller;

import com.backend.management.model.Post;
import com.backend.management.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")

public class PostController {
    @Autowired
    private PostService postService;

    // tao bai viet moi
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }


}
