package com.example.onlybuns.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.onlybuns.domain.Comment;
import com.example.onlybuns.domain.Post;
import com.example.onlybuns.service.PostService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/posts")
@PreAuthorize("hasAuthority('registered')")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    @PostMapping("/like/{postId}/{userId}")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, @PathVariable Long userId) {
        postService.addLike(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{postId}/{userId}")
    public ResponseEntity<Void> commentOnPost(@PathVariable Long postId, @PathVariable Long userId, @RequestBody Comment comment) {
        postService.addComment(postId, userId, comment);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAll")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }
    
}
