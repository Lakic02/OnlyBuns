package com.example.onlybuns.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.onlybuns.domain.Comment;
import com.example.onlybuns.domain.Post;
import com.example.onlybuns.service.PostService;



@RestController
@RequestMapping("/api/posts")
@PreAuthorize("hasAuthority('registered')")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create/{accId}")
    public ResponseEntity<Post> createPost(
            @RequestParam("description") String description,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("file") MultipartFile file, // Fajl koji se šalje
            @PathVariable Long accId) throws IOException {
        System.out.println("GRESKAAAAAAAA");
        Post post = postService.createPost(description, latitude, longitude, file, accId);
        return ResponseEntity.ok(post);
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

    @GetMapping("/getAll")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }
    
}
