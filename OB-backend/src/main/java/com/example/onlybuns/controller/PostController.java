package com.example.onlybuns.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    
    @GetMapping("/getAll")
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }

    @GetMapping("/likes/count/{postId}")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(postService.countLikesForPost(post));
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(postService.getCommentsForPost(post));
    }

    @PostMapping("/create/{accId}")
    public ResponseEntity<Post> createPost(
        @RequestParam("description") String description,
        @RequestParam("latitude") Double latitude,
        @RequestParam("longitude") Double longitude,
        @RequestParam("file") MultipartFile file, 
        @PathVariable Long accId) throws IOException {

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
        System.out.println(fileName);
        String uploadDir = System.getProperty("user.dir") + "/images/";
        System.out.println(uploadDir);
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filePath = uploadDir + fileName;
        System.out.println(filePath);
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        Post post = postService.createPost(description, latitude, longitude, fileName, accId);
        return ResponseEntity.ok(post);
    }
    
    @PutMapping("/edit/{postId}")
    public ResponseEntity<Post> editPost(@PathVariable Long postId, @RequestParam String newDescription) {
        Post updatedPost = postService.editPost(postId, newDescription);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Post> deletePost(@PathVariable Long postId) {
        Post deletedPost = postService.deletePost(postId);
        return ResponseEntity.ok(deletedPost);
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

    @GetMapping("/getFile/{postId}")
    public ResponseEntity<String> getFile(@PathVariable Long postId) {
        Post post = postService.getPostById(postId); // Dobijanje post-a iz baze
        String imagePath = post.getImagePath(); // Vraćamo samo naziv slike, npr. "2654ad81-c0a5-4053-a530-ef6a6c35a219_Screenshot 2024-10-30 223357.png"
        return new ResponseEntity<>(imagePath, HttpStatus.OK);
    }
    
}
